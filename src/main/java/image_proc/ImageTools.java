package image_proc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * @author Kevin Barnard
 * @since 2018-06-07T010:00:00
 */
public class ImageTools {

    /**
     * Threshold value for black pixel component intersection
     */
    private static final int B_MAX = 15;

    /**
     * Fetch image from internet given url
     *
     * @param url
     * @return BufferedImage
     */
    public static BufferedImage fetchImage(String url) {
        try {
            BufferedImage ret = ImageIO.read(new URL(url));
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Strips the black bars from each side of an image. Black bars do not need to be uniform, threshold specified by static value
     * Dynamic detection necessary due to inconsistency of image borders
     *
     * @param image Image to be stripped
     * @return Stripped image
     */
    public static BufferedImage removeBlackBars(BufferedImage image) {

        int width = image.getWidth();
        int height = image.getHeight();

        int startX = 0, endX = width;
        int startY = 0, endY = height;

        boolean blackBarTop = true;
        while (blackBarTop) {
            for (int x = 0; x < width; x++) {
                if (!isBlack(image, x, startY)) {
                    blackBarTop = false;
                    break;
                }
            }
            if (blackBarTop) startY++;
        }

        boolean blackBarBottom = true;
        while (blackBarBottom) {
            for (int x = 0; x < width; x++) {
                if (!isBlack(image, x, endY - 1)) {
                    blackBarBottom = false;
                    break;
                }
            }
            if (blackBarBottom) endY--;
        }

        boolean blackBarLeft = true;
        while (blackBarLeft) {
            for (int y = startY; y < endY; y++) {
                if (!isBlack(image, startX, y)) {
                    blackBarLeft = false;
                    break;
                }
            }
            if (blackBarLeft) startX++;
        }

        boolean blackBarRight = true;
        while (blackBarRight) {
            for (int y = startY; y < endY; y++) {
                if (!isBlack(image, endX - 1, y)) {
                    blackBarRight = false;
                    break;
                }
            }
            if (blackBarRight) endX--;
        }

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
        return (getR(image, x, y) <= B_MAX && getG(image, x, y) <= B_MAX && getB(image, x, y) <= B_MAX);
    }

}
