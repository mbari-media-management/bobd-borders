package imageutils;

import java.awt.image.BufferedImage;

/**
 * @author Kevin Barnard
 * @since 2018-06-07T010:00:00
 */
public class ImageUtils {

    /**
     * Threshold value for black pixel component intersection
     */
    private static final int B_MAX_DEFAULT = 30;

    /**
     * Strips the black bars from each side of an image. Black bars do not need to be uniform, threshold specified by static value
     * Dynamic detection necessary due to inconsistency of image borders
     *
     * @param image Image to be stripped
     * @return Stripped image
     */
    public static BufferedImage removeBorders(BufferedImage image) {

        // Start and end bounds
        int startX = 0, endX = image.getWidth();
        int startY = 0, endY = image.getHeight();

        // Bar existence
        boolean barTop = true,
                barLeft = true,
                barBottom = true,
                barRight = true;

        // Top bar
        while (barTop) {
            for (int x = 0; x < endX; x++) {
                if (!isBlack(image, x, startY)) {
                    barTop = false;
                }
            }
            if (barTop) startY++;
        }

        // Left bar
        while (barLeft) {
            for (int y = startY; y < endY; y++) {
                if (!isBlack(image, startX, y)) {
                    barLeft = false;
                }
            }
            if (barLeft) startX++;
        }

        // Bottom bar
        while (barBottom) {
            for (int x = 0; x < endX; x++) {
                if (!isBlack(image, x, endY - 1)) {
                    barBottom = false;
                }
            }
            if (barBottom) endY--;
        }

        // Right bar
        while (barRight) {
            for (int y = startY; y < endY; y++) {
                if (!isBlack(image, endX - 1, y)) {
                    barRight = false;
                }
            }
            if (barRight) endX--;
        }

        // Replace image
        image = image.getSubimage(startX, startY, endX - startX, endY - startY);

        return image;
    }

    private static int getR(BufferedImage image, int x, int y) {
        return image.getRGB(x, y) >> 16 & 0xff;
    }

    private static int getG(BufferedImage image, int x, int y) {
        return image.getRGB(x, y) >> 8 & 0xff;
    }

    private static int getB(BufferedImage image, int x, int y) {
        return image.getRGB(x, y) & 0xff;
    }

    /**
     * Determines if a provided pixel in BufferedImage image at (x, y) is "black"
     * If all RGB components are within threshold, pixel considered black
     * This processing is necessary as input image black borders are inconsistent and not truly black
     *
     * @param image Image to be read
     * @param x     coordinate
     * @param y     coordinate
     * @return true if pixel is "black"
     */
    private static boolean isBlack(BufferedImage image, int x, int y) {
        return (
                getR(image, x, y) <= B_MAX_DEFAULT &&
                        getG(image, x, y) <= B_MAX_DEFAULT &&
                        getB(image, x, y) <= B_MAX_DEFAULT
        );
    }

}
