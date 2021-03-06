package texturedsphere

import java.awt.{Point, Dimension}
import java.awt.event._
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
      def run() {
        val glp = GLProfile.get(GLProfile.GL2)
        val glCapabilities: GLCapabilities = new GLCapabilities(glp)
        val canvas = new TexturedSphere(glCapabilities)
        canvas.setPreferredSize(new Dimension(640, 640))
        val animator = new FPSAnimator(canvas, 60, true)
        val frame = new JFrame
        frame.getContentPane.add(canvas)
        frame.addWindowListener(new WindowAdapter {
          override def windowClosing(e: WindowEvent) {
            new Thread {
              override def run() {
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

  private def loadTexture(gl: GL2, filename: String): Texture = {
    val stream = getClass.getResourceAsStream(filename)
    val data = TextureIO.newTextureData(GLProfile.getDefault, stream, false, "jpg")
    val tex = TextureIO.newTexture(data)
    tex.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT)
    tex.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT)
    tex.setTexParameteri(gl, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR)
    tex.setTexParameteri(gl, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR)
    tex
  }
}

class TexturedSphere(caps: GLCapabilities) extends GLCanvas(caps) with GLEventListener {

  import TexturedSphere._

  private val glu = new GLU()

  private var earthTexture: Texture = null
  private var nightTexture: Texture = null
  private var specTexture: Texture = null

  private var programID = -1

  @volatile private var LastRot = Matrix4f.Identity
  @volatile private var ThisRot = Matrix4f.Identity
  private val matrix = new Array[Float](16)

  private val arcBall = new ArcBall(640.0f, 640.0f)  // NEW: ArcBall Instance

  addGLEventListener(this)

  addMouseListener(new MouseAdapter {

    override def mousePressed(e: MouseEvent): Unit = {
      LastRot = ThisRot
      arcBall.click( e.getPoint )    // Update Start Vector And Prepare For Dragging
    }

    override def mouseReleased(e: MouseEvent): Unit = {
      println("mouse released: " + e.getPoint)
    }
  })

  addMouseMotionListener(new MouseMotionAdapter {
    override def mouseDragged(e: MouseEvent): Unit = {
      // Update End Vector And Get Rotation As Quaternion
      val quat = arcBall.drag( e.getPoint)
      ThisRot = Matrix4f.rotation(quat)  // Convert Quaternion Into Matrix3fT
      ThisRot = ThisRot.mul(LastRot) // Accumulate Last Rotation Into This One
    }
  })

  override def init(drawable: GLAutoDrawable): Unit = {

    // Start Of User Initialization
    LastRot = Matrix4f.Identity // Reset Rotation
    ThisRot = Matrix4f.Identity // Reset Rotation
    ThisRot.get(matrix)

    val gl = drawable.getGL.getGL2
    drawable.setGL(new DebugGL2(gl)) // enable stack traces

    this.programID = newProgram(drawable.getGL.getGL2)

    // Load textures.
    earthTexture = loadTexture(gl, "earthmap_day.jpg")
    nightTexture = loadTexture(gl, "earthlights.jpg")
    specTexture = loadTexture(gl, "earthmap_specular.jpg")

    // Global settings.
    gl.glEnable(GL_DEPTH_TEST)
    gl.glDepthFunc(GL_LEQUAL)
    gl.glShadeModel(GL_SMOOTH)
    gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST)
    gl.glClearColor(0f, 0f, 0f, 0f)
  }

  override def display(drawable: GLAutoDrawable): Unit = {
    ThisRot.get(matrix)

    val gl = drawable.getGL.getGL2
    gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

    setCamera(gl, glu, 30)

    gl.glUseProgram(this.programID)

    val lightLoc = gl.glGetUniformLocation(this.programID, "LightPosition")
    gl.glUniform3f(lightLoc, 100.0f, 0.0f, 100.0f)

    var texLoc = gl.glGetUniformLocation(this.programID, "EarthDay")
    gl.glUniform1i(texLoc, 0)

    texLoc = gl.glGetUniformLocation(this.programID, "EarthNight")
    gl.glUniform1i(texLoc, 1)

    texLoc = gl.glGetUniformLocation(this.programID, "EarthCloudGloss")
    gl.glUniform1i(texLoc, 2)

    // Apply texture.
    gl.glActiveTexture(GL_TEXTURE0)
    earthTexture.enable(gl)
    earthTexture.bind(gl)

    gl.glActiveTexture(GL_TEXTURE1)
    nightTexture.enable(gl)
    nightTexture.bind(gl)

    gl.glActiveTexture(GL_TEXTURE2)
    specTexture.enable(gl)
    specTexture.bind(gl)

    gl.glPushMatrix();                  // NEW: Prepare Dynamic Transform
    gl.glMultMatrixf(matrix, 0)


    gl.glRotatef(-90, 1, 0, 0)

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

    gl.glPopMatrix()
  }

  override def reshape(drawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int): Unit = {
    val gl = drawable.getGL.getGL2
    gl.glViewport(0, 0, width, height)
    //*NEW* Update mouse bounds for arcball
    arcBall.setBounds(width, height)
  }

  override def dispose(drawable: GLAutoDrawable): Unit = {

  }

  private def setCamera(gl: GL2, glu: GLU, distance: Float) {
    // Change to projection matrix.
    gl.glMatrixMode(GL_PROJECTION)
    gl.glLoadIdentity()

    // Perspective.
    val widthHeightRatio = getWidth.toFloat / getHeight.toFloat
    //glu.gluPerspective(45, widthHeightRatio, 1, 1000)
    gl.glOrtho(-10, 10, -10, 10, 1, 1000)
    glu.gluLookAt(0, 0, distance, 0, 0, 0, 0, 1, 0)

    // Change back to model view matrix.
    gl.glMatrixMode(GL_MODELVIEW)
    gl.glLoadIdentity()
  }

  private def newProgram(gl: GL2): Int = {
    val v: Int = newShaderFromCurrentClass(gl, "vertex.shader", VertexShader)
    val f: Int = newShaderFromCurrentClass(gl, "fragment.shader", FragmentShader)
    println(getShaderInfoLog(gl, v))
    println(getShaderInfoLog(gl, f))
    val p: Int = createProgram(gl, v, f)
    printProgramInfoLog(gl, p)
    p
  }
}
