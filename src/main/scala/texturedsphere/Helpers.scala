package texturedsphere

import java.io.{IOException, InputStreamReader, BufferedReader, InputStream}
import javax.media.opengl.GL2
import javax.media.opengl.GL2ES2._

object Helpers {

  sealed abstract class ShaderType
  case object VertexShader extends ShaderType
  case object FragmentShader extends ShaderType


  /** Retrieves the info log for the shader */
  def getShaderInfoLog(gl: GL2, obj: Int): String = {
    val logLen: Int = getShaderParameter(gl, obj, GL_INFO_LOG_LENGTH)
    if (logLen <= 0) return ""
    val retLength: Array[Int] = new Array[Int](1)
    val bytes: Array[Byte] = new Array[Byte](logLen + 1)
    gl.glGetShaderInfoLog(obj, logLen, retLength, 0, bytes, 0)
    val logMessage: String = new String(bytes)
    String.format("ShaderLog: %s", logMessage)
  }

  /** Get a shader parameter value. See 'glGetShaderiv' */
  private def getShaderParameter(gl: GL2, obj: Int, paramName: Int): Int = {
    val params = new Array[Int](1)
    gl.glGetShaderiv(obj, paramName, params, 0)
    params(0)
  }

  /** Retrieves the info log for the program */
  def printProgramInfoLog(gl: GL2, obj: Int): String = {
    val logLen: Int = getProgramParameter(gl, obj, GL_INFO_LOG_LENGTH)
    if (logLen <= 0) return ""
    val retLength: Array[Int] = new Array[Int](1)
    val bytes: Array[Byte] = new Array[Byte](logLen + 1)
    gl.glGetProgramInfoLog(obj, logLen, retLength, 0, bytes, 0)
    val logMessage: String = new String(bytes)
    logMessage
  }

  /** Gets a program parameter value */
  def getProgramParameter(gl: GL2, obj: Int, paramName: Int): Int = {
    val params = new Array[Int](1)
    gl.glGetProgramiv(obj, paramName, params, 0)
    params(0)
  }

  protected def loadStringFileFromCurrentPackage(fileName: String): String = {
    val stream: InputStream = this.getClass.getResourceAsStream(fileName)
    val reader: BufferedReader = new BufferedReader(new InputStreamReader(stream))
    val strBuilder: StringBuilder = new StringBuilder
    try {
      var line: String = reader.readLine
      while (line != null) {
        strBuilder.append(line + "\n")
        line = reader.readLine
      }
      reader.close()
      stream.close()
    } catch {
      case e: IOException => e.printStackTrace()
    }
    strBuilder.toString
  }

  def createProgram(gl: GL2, vertexShaderId: Int, fragmentShaderId: Int): Int = {
    val programId: Int = gl.glCreateProgram
    gl.glAttachShader(programId, vertexShaderId)
    gl.glAttachShader(programId, fragmentShaderId)
    gl.glLinkProgram(programId)
    programId
  }

  def newShaderFromCurrentClass(gl: GL2, fileName: String, typ: ShaderType): Int = {
    val shaderSource = loadStringFileFromCurrentPackage(fileName)
    val shaderType: Int = if (typ == VertexShader) GL_VERTEX_SHADER else GL_FRAGMENT_SHADER
    val id: Int = gl.glCreateShader(shaderType)
    gl.glShaderSource(id, 1, Array[String](shaderSource), null)
    gl.glCompileShader(id)
    id
  }
}
