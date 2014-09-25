package texturedsphere;

import java.util.Locale;

/**
 * Represents an immutable vector in 3-dimensional space.
 * User: Chris Peat
 * Date: 24/04/12
 */
public final class Vector3 {
	public final static Vector3 ZERO = new Vector3(0, 0, 0);
	public final static Vector3 NaN = new Vector3(Double.NaN, Double.NaN, Double.NaN);
	public final static Vector3 UNDEFINED = new Vector3(Double.NaN, Double.NaN, Double.NaN);

    private final double _x;
    private final double _y;
    private final double _z;

    /**
     * Iniitialise with cartesian components.
     */
    public Vector3(double x, double y, double z) {
        _x = x;
        _y = y;
        _z = z;
    }

    /**
     * Initialise to unit vector given azimuth and elevation in radians.
     *
     * @param azimuth   the azimuth angle in radians, measured anti-clockwise from the x-axis
     * @param elevation the elevation angle in radians measured from the xy plane
     */
    public Vector3(double azimuth, double elevation) {
        double CosElev = Math.cos(elevation);
        _x = Math.cos(azimuth) * CosElev;
        _y = Math.sin(azimuth) * CosElev;
        _z = Math.sin(elevation);
    }

    /**
     * Creates a new vector with every component scaled by the given factor.
     */
    public Vector3 scale(double scaleFactor) {
        return new Vector3(_x * scaleFactor, _y * scaleFactor, _z * scaleFactor);
    }

    public double getX() {
        return _x;
    }

    public double getY() {
        return _y;
    }

    public double getZ() {
        return _z;
    }

    public double x() {
        return _x;
    }

    public double y() {
        return _y;
    }

    public double z() {
        return _z;
    }

    /**
     * Gets the square of the magnitude.
     */
    public double magnitudeSquared() {
        return _x * _x + _y * _y + _z * _z;
    }

    /**
     * Gets the magnitude (length) of the vector.
     */
    public double magnitude() {
        return Math.sqrt(magnitudeSquared());
    }

    public double latitude() {
        return Math.asin(_z / magnitude());
    }

    public double longitude() {
        return Math.atan2(_y, _x);
    }

    /**
     * Creates a new vector with the same orientation but unit length.
     */
    public Vector3 normalize() {
        return scale(1 / magnitude());
    }

    public Vector3 add(Vector3 second) {
        return new Vector3(_x + second._x, _y + second._y, _z + second._z);
    }

    public Vector3 subtract(Vector3 second) {
        return new Vector3(_x - second._x, _y - second._y, _z - second._z);
    }

    public Vector3 rotateAboutXAxis(double angle) {
        double sine, cosine;
        sine = Math.sin(angle);
        cosine = Math.cos(angle);
        return new Vector3(_x, cosine * _y - sine * _z, sine * _y + cosine * _z);
    }

    public Vector3 rotateAboutZAxis(double angle) {
        double sine, cosine;
        sine = Math.sin(angle);
        cosine = Math.cos(angle);
        return new Vector3(cosine * _x - sine * _y, sine * _x + cosine * _y, _z);
    }

    public double dotProduct(Vector3 second) {
        return (_x * second._x + _y * second._y + _z * second._z);
    }

    public Vector3 vectorProduct(Vector3 second) {
        return new Vector3(_y * second._z - _z * second._y,
                _z * second._x - _x * second._z,
                _x * second._y - _y * second._x);
    }

    public double getElement(int i) {
        switch (i) {
            case 0:
                return _x;
            case 1:
                return _y;
            case 2:
                return _z;
        }
        return Double.NaN;
    }

    public double angleBetween(Vector3 Second) {
        double Cosine;
        double Mag1 = magnitude();
        double Mag2 = Second.magnitude();
        double MagProduct = Mag1 * Mag2;

        if (MagProduct > 0.0) {
            Cosine = dotProduct(Second) / MagProduct;
            if (Math.abs(Cosine) <= 1.0)
                return Math.acos(Cosine);
            else
                return 0.0;
        } else
            return 0.0;
    }

    /**
     * Gets azimuth in degrees measured clockwise from North.
     */
    public double getAzimuthInDegrees() {
        double az = 90 - longitude() / Constants.DEGTORAD;

        if (az < 0)
            az += 360.0;

        return az;
    }

    /**
     * Gets elevation in degrees measured updwards from the horizon.
     */
    public double getElevationInDegrees() {
        return latitude() / Constants.DEGTORAD;
    }

    @Override
    public String toString() {
    	return String.format(Locale.US, "Vector3(%.02f, %.02f, %.02f)", _x, _y, _z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector3 vector3 = (Vector3) o;

        if (Double.compare(vector3._x, _x) != 0) return false;
        if (Double.compare(vector3._y, _y) != 0) return false;
        if (Double.compare(vector3._z, _z) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(_x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(_y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(_z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
