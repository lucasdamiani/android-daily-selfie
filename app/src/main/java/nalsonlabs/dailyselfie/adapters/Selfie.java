package nalsonlabs.dailyselfie.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import nalsonlabs.dailyselfie.MockedLocationUtils;
import nalsonlabs.dailyselfie.R;

/**
 * Container for a Selfie, with some helper methods to display information about a given selfie.
 *
 * @author Lucas Damiani
 */
public class Selfie implements Serializable {

    private static final int sRadius = 500;
    private File imageFile;
    private boolean hasLocationAvailable;
    private float latitude;
    private float longitude;

    /**
     * Selfie's constructor.
     *
     * @param imageFile File related to the given selfie.
     */
    public Selfie(File imageFile) {
        this.imageFile = imageFile;
    }

    public File getImageFile() {
        return imageFile;
    }

    /**
     * Gets the file name of the selfie, without the extension.
     *
     * @return Selfie's file name.
     */
    public String getImageFileName() {
        String fileName = getImageFile().getName().replaceFirst("[.][^.]+$", "");

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyyMMddHHmmss").parse(fileName);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
    }

    public float getLatitude() {
        // Non optimized version of the method.
        prepareLocation();
        return latitude;
    }

    public float getLongitude() {
        // Non optimized version of the method.
        prepareLocation();
        return longitude;
    }

    private void prepareLocation() {
        try {
            if (hasLocationAvailable)
                return;

            ExifInterface exifInterface = new ExifInterface(getImageFileName());
            float[] latLong = new float[2];
            if (!exifInterface.getLatLong(latLong))
                MockedLocationUtils.generateRandomLocation(sRadius, latLong);

            latitude = latLong[0];
            longitude = latLong[1];
            hasLocationAvailable = true;
        } catch (IOException e) {
            hasLocationAvailable = false;
        }
    }

    public Bitmap getThumbnail() {
        return getThumbnail(250,250);
    }


    /**
     * Gets a thumbnail {@link Bitmap} for this Selfie.
     *
     * @param targetWidth  Target width of the thumbnail.
     * @param targetHeight Target height of the thumbnail.
     * @return Bitmap containing the thumbnail.
     */
    public Bitmap getThumbnail(int targetWidth, int targetHeight) {
        if (getImageFile() == null)
            return null;

        String imagePath = getImageFile().getPath();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bitmapOptions);
        int imageWidth = bitmapOptions.outWidth;
        int imageHeight = bitmapOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(imageWidth / targetWidth, imageHeight / targetHeight);

        // Decode the image file into a Bitmap sized to fill the View
        bitmapOptions.inJustDecodeBounds = false;
        bitmapOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeFile(imagePath, bitmapOptions);
    }
}
