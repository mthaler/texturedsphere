package texturedsphere

import java.awt.Dimension
import java.awt.event.{WindowEvent, WindowAdapter}
import javax.media.opengl.GL._
import javax.media.opengl.GL2ES1._
import javax.media.opengl.fixedfunc.GLLightingFunc._
import javax.media.opengl.fixedfunc.GLMatrixFunc._
import javax.media.opengl._
import javax.media.opengl.awt.GLCanvas
import javax.media.opengl.glu.GLU
import javax.swing.{JFrame, SwingUtilities}

import com.jogamp.opengl.util.FPSAnimator
import com.jogamp.opengl.util.texture.{Texture, TextureIO}

import Helpers._

object TexturedSphere {

  def main(args: Array[String]): Unit = {
    // Run the GUI codes in the event-dispatching thread for thread safety


    SwingUtilities.invokeLater(new Runnable {
      def run {
        val canvas = new TexturedSphere
        canvas.setPreferredSize(new Dimension(640, 480))
        val animator = new FPSAnimator(canvas, 60, true)
        val frame = new JFrame
        frame.getContentPane.add(canvas)
        frame.addWindowListener(new WindowAdapter {
          override def windowClosing(e: WindowEvent) {
            new Thread {
              override def run {
                if (animator.isStarted) animator.stop
                System.exit(0)
              }
            }.start()
          }
        })
        frame.setTitle("TexturedSphere")
        frame.pack()
        frame.setVisible(true)
        animator.start()
      }
    })
  }
}

class TexturedSphere extends GLCanvas with GLEventListener {

  addGLEventListener(this)

  private val glu = new GLU()
  private var earthTexture: Texture = null

  override def init(drawable: GLAutoDrawable): Unit = {

    // Load earth texture.
    val stream = getClass().getResourceAsStream("earthmap1k.jpg")
    val data = TextureIO.newTextureData(GLProfile.getDefault, stream, false, "jpg")
    earthTexture = TextureIO.newTexture(data)

    val gl = drawable.getGL().getGL2
    drawable.setGL(new DebugGL2(gl)) // enable stack traces

    // Global settings.
    gl.glEnable(GL_DEPTH_TEST)
    gl.glDepthFunc(GL_LEQUAL)
    gl.glShadeModel(GL_SMOOTH)
    gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST)
    gl.glClearColor(0f, 0f, 0f, 1f)
  }

  override def display(drawable: GLAutoDrawable): Unit = {
    val gl = drawable.getGL.getGL2
    gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

    setCamera(gl, glu, 30)

    // Prepare light parameters.
    val SHINE_ALL_DIRECTIONS = 1.0f
    val lightPos = Array[Float](-30, 0, 0, SHINE_ALL_DIRECTIONS)
    val lightColorAmbient = Array[Float](0.2f, 0.2f, 0.2f, 1f)
    val lightColorSpecular = Array[Float](0.8f, 0.8f, 0.8f, 1f)

    // Set light parameters.
    gl.glLightfv(GL_LIGHT1, GL_POSITION, lightPos, 0)
    gl.glLightfv(GL_LIGHT1, GL_AMBIENT, lightColorAmbient, 0)
    gl.glLightfv(GL_LIGHT1, GL_SPECULAR, lightColorSpecular, 0)

    // Enable lighting in GL.
    gl.glEnable(GL_LIGHT1)
    gl.glEnable(GL_LIGHTING)

    // Set material properties.
    val rgba = Array[Float](1f, 1f, 1f)
    gl.glMaterialfv(GL.GL_FRONT, GL_AMBIENT, rgba, 0)
    gl.glMaterialfv(GL.GL_FRONT, GL_SPECULAR, rgba, 0)
    gl.glMaterialf(GL.GL_FRONT, GL_SHININESS, 0.5f)

    // Apply texture.
    earthTexture.enable(gl)
    earthTexture.bind(gl)

    // Draw sphere (possible styles: FILL, LINE, POINT).
    gl.glColor3f(0.3f, 0.5f, 1f)
    val earth = glu.gluNewQuadric()
    glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL)
    glu.gluQuadricNormals(earth, GLU.GLU_FLAT)
    glu.gluQuadricOrientation(earth, GLU.GLU_OUTSIDE)
    glu.gluQuadricTexture(earth, true)
    val radius = 6.378f
    val slices = 256
    val stacks = 256
    glu.gluSphere(earth, radius, slices, stacks)
    glu.gluDeleteQuadric(earth)
  }

  override def reshape(drawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int): Unit = {
    val gl = drawable.getGL().getGL2
    gl.glViewport(0, 0, width, height)
  }

  override def dispose(drawable: GLAutoDrawable): Unit = {

  }

  private def setCamera(gl: GL2, glu: GLU, distance: Float) {
    // Change to projection matrix.
    gl.glMatrixMode(GL_PROJECTION);
    gl.glLoadIdentity()

    // Perspective.
    val widthHeightRatio = getWidth().toFloat / getHeight().toFloat
    glu.gluPerspective(45, widthHeightRatio, 1, 1000)
    glu.gluLookAt(0, 0, distance, 0, 0, 0, 0, 1, 0)

    // Change back to model view matrix.
    gl.glMatrixMode(GL_MODELVIEW)
    gl.glLoadIdentity()
  }

  private def newProgram(gl: GL3): Int = {
    val v: Int = newShaderFromCurrentClass(gl, "vertex.shader", VertexShader)
    val f: Int = newShaderFromCurrentClass(gl, "fragment.shader", FragmentShader)
    println(getShaderInfoLog(gl, v))
    println(getShaderInfoLog(gl, f))
    val p: Int = createProgram(gl, v, f)
    //gl.glBindFragDataLocation(p, 0, "outColor")
    printProgramInfoLog(gl, p)
//    this.vertexLoc = gl.glGetAttribLocation(p, "position")
//    this.colorLoc = gl.glGetAttribLocation(p, "color")
//    this.projMatrixLoc = gl.glGetUniformLocation(p, "projMatrix")
//    this.viewMatrixLoc = gl.glGetUniformLocation(p, "viewMatrix")
    p
  }
}
