package main;

import imageutils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Kevin Barnard
 * @since 2018-06-11T010:30:00
 */
public class Main {

    public static void main(String[] args) {

        File[] folders = parseArgs(args);

        if (folders == null) {
            System.out.println("\n--- Process failed, exiting. ---");
            return;
        }

        processImages(folders[0], folders[1]);

        System.out.println("\n--- Process completed successfully. ---");

    }

    private static void processImages(File inputFolder, File outputFolder) {

        File[] inputFiles = inputFolder.listFiles();

        assert inputFiles != null;
        for (File inputFile : inputFiles) {

            if (inputFile.isDirectory()) { // Process subdirectory
                File subDir = new File(String.join(
                        "/",
                        outputFolder.getAbsolutePath(),
                        inputFile.getAbsolutePath().replace(
                                inputFolder.getAbsolutePath(),
                                ""
                        )
                ));
                if (!subDir.exists() && !subDir.mkdir())
                    System.err.println("Error creating subdirectory " + subDir.getPath());
                processImages(inputFile, subDir);
            } else { // Process image
                String fileName = inputFile.getName();

                if (!fileName.substring(fileName.length() - 4).equals(".png")) {
                    System.out.println(fileName + " is not a .png, skipping");
                    continue;
                }

                System.out.print("Processing " + inputFile.getPath());

                long inputSize = inputFile.length();

                long start = System.nanoTime();

                File outputFile = new File(outputFolder, fileName);
                try {
                    BufferedImage processedImage = ImageUtils.removeBorders(ImageIO.read(inputFile));
                    ImageIO.write(processedImage, "png", outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(" " + Math.round(inputSize / 1000. / ((System.nanoTime() - start)/1000000000.) * 10)/10. + " kB/s");

            }
        }

    }

    private static File[] parseArgs(String[] args) {

        String readPath, writePath;

        // Verify command entered correctly
        try {
            readPath = args[0];
            writePath = args[1];
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Incorrect usage. Follow:\n\tjava -jar bobd-borders.jar /path/to/images /path/to/output");
            return null;
        }

        // Verify paths
        File[] folders = new File[2];
        try {
            folders[0] = new File(readPath);
            folders[1] = new File(writePath);
        } catch (NullPointerException e) {
            System.out.println("Path not valid. Please check program arguments.");
            return null;
        }

        // Verify input folder exists and is valid
        boolean success = true;
        if (!folders[0].isDirectory()) {
            System.out.println(folders[0].getAbsolutePath() + " is not a folder.");
            success = false;
        }
        if (!success) return null;

        if (!folders[1].exists() && !folders[1].mkdir())
            System.err.println("Error creating directory " + folders[1].getAbsolutePath());

        return folders;

    }

}
