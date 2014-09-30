package texturedsphere

object Matrix4f {
  val Identity = Matrix4f(
                  1.0f, 0.0f, 0.0f, 0.0f,
                  0.0f, 1.0f, 0.0f, 0.0f,
                  0.0f, 0.0f, 1.0f, 0.0f,
                  0.0f, 0.0f, 0.0f, 1.0f
                 )

  def rotation(q1: Quat4f): Matrix4f = {
    var n: Float = 0.0f
    var s: Float = 0.0f
    var xs: Float = 0.0f
    var ys: Float = 0.0f
    var zs: Float = 0.0f
    var wx: Float = 0.0f
    var wy: Float = 0.0f
    var wz: Float = 0.0f
    var xx: Float = 0.0f
    var xy: Float = 0.0f
    var xz: Float = 0.0f
    var yy: Float = 0.0f
    var yz: Float = 0.0f
    var zz: Float = 0.0f
    n = (q1.x * q1.x) + (q1.y * q1.y) + (q1.z * q1.z) + (q1.w * q1.w)
    s = if ((n > 0.0f)) (2.0f / n) else 0.0f
    xs = q1.x * s
    ys = q1.y * s
    zs = q1.z * s
    wx = q1.w * xs
    wy = q1.w * ys
    wz = q1.w * zs
    xx = q1.x * xs
    xy = q1.x * ys
    xz = q1.x * zs
    yy = q1.y * ys
    yz = q1.y * zs
    zz = q1.z * zs
    Matrix4f(
      1.0f - (yy + zz), xy - wz, xz + wy, 0f,
      xy + wz, 1.0f - (xx + zz), yz - wx, 0f,
      xz - wy, yz + wx, 1.0f - (xx + yy), 0f,
      0f, 0f, 0f, 1f
    )
  }
}

case class Matrix4f(
                     m00: Float, m01: Float, m02: Float, m03: Float,
                     m10: Float, m11: Float, m12: Float, m13: Float,
                     m20: Float, m21: Float, m22: Float, m23: Float,
                     m30: Float, m31: Float, m32: Float, m33: Float
                   ) {

   def get(dest: Array[Float]) {
    dest(0) = m00
    dest(1) = m10
    dest(2) = m20
    dest(3) = m30
    dest(4) = m01
    dest(5) = m11
    dest(6) = m21
    dest(7) = m31
    dest(8) = m02
    dest(9) = m12
    dest(10) = m22
    dest(11) = m32
    dest(12) = m03
    dest(13) = m13
    dest(14) = m23
    dest(15) = m33
  }

  /**
   * Sets the value of this matrix to the result of multiplying
   * the two argument matrices together.
   *
   * @param m2 the second matrix
   */
  final def mul(m2: Matrix4f): Matrix4f = {
    return new Matrix4f(m00 * m2.m00 + m01 * m2.m10 + m02 * m2.m20 + m03 * m2.m30,
      m00 * m2.m01 + m01 * m2.m11 + m02 * m2.m21 + m03 * m2.m31,
      m00 * m2.m02 + m01 * m2.m12 + m02 * m2.m22 + m03 * m2.m32,
      m00 * m2.m03 + m01 * m2.m13 + m02 * m2.m23 + m03 * m2.m33,
      m10 * m2.m00 + m11 * m2.m10 + m12 * m2.m20 + m13 * m2.m30,
      m10 * m2.m01 + m11 * m2.m11 + m12 * m2.m21 + m13 * m2.m31,
      m10 * m2.m02 + m11 * m2.m12 + m12 * m2.m22 + m13 * m2.m32,
      m10 * m2.m03 + m11 * m2.m13 + m12 * m2.m23 + m13 * m2.m33,
      m20 * m2.m00 + m21 * m2.m10 + m22 * m2.m20 + m23 * m2.m30,
      m20 * m2.m01 + m21 * m2.m11 + m22 * m2.m21 + m23 * m2.m31,
      m20 * m2.m02 + m21 * m2.m12 + m22 * m2.m22 + m23 * m2.m32,
      m20 * m2.m03 + m21 * m2.m13 + m22 * m2.m23 + m23 * m2.m33,
      m30 * m2.m00 + m31 * m2.m10 + m32 * m2.m20 + m33 * m2.m30,
      m30 * m2.m01 + m31 * m2.m11 + m32 * m2.m21 + m33 * m2.m31,
      m30 * m2.m02 + m31 * m2.m12 + m32 * m2.m22 + m33 * m2.m32,
      m30 * m2.m03 + m31 * m2.m13 + m32 * m2.m23 + m33 * m2.m33)
  }
}