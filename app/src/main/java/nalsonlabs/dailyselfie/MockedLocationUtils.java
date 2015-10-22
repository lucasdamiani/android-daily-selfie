package nalsonlabs.dailyselfie;

import java.util.Random;

/**
 * Created by lucasdamiani on 25/06/15.
 */
public final class MockedLocationUtils {

    public static float DEFAULT_LATITUDE = -25.453797f;
    public static float DEFAULT_LONGITUDE = -49.362175f;

    private MockedLocationUtils() {}

    public static void generateRandomLocation(int radius, float[] latLong) {
        generateRandomLocation(DEFAULT_LATITUDE, DEFAULT_LONGITUDE, radius, latLong);
    }

    public static void generateRandomLocation(double baseLatitude, double baseLongitude, int radius, float[] latLong) {
        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(baseLatitude);

        double foundLongitude = new_x + baseLongitude;
        double foundLatitude = y + baseLatitude;

        latLong[0] = new Float(foundLatitude);
        latLong[1] = new Float(foundLongitude);
    }
}
