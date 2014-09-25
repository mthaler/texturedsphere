package texturedsphere

import java.awt._

object ArcBall {
  private final val Epsilon: Float = 1.0e-5f
}

class ArcBall(NewWidth: Float, NewHeight: Float) {

  import ArcBall._

  private var StVec = new Vector3f
  private var EnVec = new Vector3f
  private var adjustWidth: Float = 0.0f
  private var adjustHeight: Float = 0.0f

  setBounds(NewWidth, NewHeight)

  def mapToSphere(point: Point, vector: Vector3f) {
    // Copy paramter into temp point and adjust point coords and scale down to range of [-1 ... 1]
    val tempPoint = Point2f((point.x * this.adjustWidth) - 1.0f, 1.0f - (point.y * this.adjustHeight))

    // Compute the square of the length of the vector to the point from the center
    val length: Float = (tempPoint.x * tempPoint.x) + (tempPoint.y * tempPoint.y)

    // If the point is mapped outside of the sphere... (length > radius squared)
    if (length > 1.0f) {
      // Compute a normalizing factor (radius / sqrt(length))
      val norm = (1.0 / Math.sqrt(length)).asInstanceOf[Float]

      // Return the "normalized" vector, a point on the sphere
      vector.x = tempPoint.x * norm
      vector.y = tempPoint.y * norm
      vector.z = 0.0f
    } else {
      // Return a vector to a point mapped inside the sphere
      // sqrt(radius squared - length)
      vector.x = tempPoint.x
      vector.y = tempPoint.y
      vector.z = Math.sqrt(1.0f - length).asInstanceOf[Float]
    }
  }

  def setBounds(NewWidth: Float, NewHeight: Float) {
    assert(((NewWidth > 1.0f) && (NewHeight > 1.0f)))

    // Set adjustment factor for width/height
    adjustWidth = 1.0f / ((NewWidth - 1.0f) * 0.5f)
    adjustHeight = 1.0f / ((NewHeight - 1.0f) * 0.5f)
  }

  def click(NewPt: Point) {
    mapToSphere(NewPt, this.StVec)
  }

  // Mouse drag, calculate rotation
  def drag(NewPt: Point, NewRot: Quat4f) {

    // Map the point to the sphere
    this.mapToSphere(NewPt, EnVec)

    // Return the quaternion equivalent to the rotation
    if (NewRot != null) {
      val Perp: Vector3f = new Vector3f

      // Compute the vector perpendicular to the begin and end vectors
      Vector3f.cross(Perp, StVec, EnVec)

      // Compute the length of the perpendicular vector
      if (Perp.length > Epsilon) {
        // We're ok, so return the perpendicular vector as the transform
        // after all
        NewRot.x = Perp.x
        NewRot.y = Perp.y
        NewRot.z = Perp.z
        // In the quaternion values, w is cosine (theta / 2),
        // where theta is rotation angle
        NewRot.w = Vector3f.dot(StVec, EnVec)
      } else {
        // The begin and end vectors coincide, so return an identity transform
        NewRot.x = 0.0f
        NewRot.y = 0.0f
        NewRot.z = 0.0f
        NewRot.w = 0.0f
      }
    }
  }
}