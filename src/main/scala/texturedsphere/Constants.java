package texturedsphere;

/**
 * Constants used by AstroLib.
 * User: Chris Peat
 * Date: 26/04/12
 */
public class Constants {
    public static final double TWOPI = Math.PI * 2;
    public static final double DEGTORAD = Math.PI / 180;
    public static final double RATORAD = Math.PI / 12;
    public static final double OBLIQUITY_2000 = 23.43929111 * DEGTORAD;
    public static final double ARCSECTORAD = DEGTORAD / 3600.0; // Arc seconds to radians conversion factor
    /**
     * Astronomical unit in km
     */
    public static final double AU = 149597870.66;
    /**
     * Speed of light in km/s
     */
    public static final double CLIGHT = 299792.458;
    /**
     * 1 second as a fraction of a day
     */
    public static final double ONE_SECOND = 1.0 / 180.0;
    /**
     * Light-year in km
     */
    public static final double LIGHT_YEAR = CLIGHT * 365.25 * 86400.0;

}
