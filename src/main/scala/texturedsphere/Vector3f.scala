package texturedsphere

object Vector3f {
  val Zero = new Vector3f(0.0f, 0.0f, 0.0f)

  def cross(v1: Vector3f, v2: Vector3f): Vector3f = {
    val x: Float = (v1.y * v2.z) - (v1.z * v2.y)
    val y: Float = (v1.z * v2.x) - (v1.x * v2.z)
    val z: Float = (v1.x * v2.y) - (v1.y * v2.x)
    new Vector3f(x, y, z)
  }

  def dot(v1: Vector3f, v2: Vector3f) = (v1.x * v2.x) + (v1.y * v2.y) + (v1.z + v2.z)
}

case class Vector3f(x: Float, y: Float, z: Float) {

  def length = math.sqrt(x * x + y * y + z * z).asInstanceOf[Float]
}