package main;

import image_proc.ImageTools;
import sql.Query;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Kevin Barnard
 * @since 2018-06-07T010:00:00
 */
class App {

    private Query query;

    /**
     * Parser constructor
     *
     * @param config String array of parameters
     */
    App(String[] config) {
        this(
                config[0],                                  // SQL statement
                Boolean.parseBoolean(config[1])             // Verbosity
        );
    }

    /**
     * Explicit application constructor
     *
     * @param verbose Verbosity on/off
     */
    App(String statement, boolean verbose) {

        long last = System.currentTimeMillis();

        this.init();
        boolean success = this.run(statement, verbose);

        long s = (System.currentTimeMillis() - last) / 1000;
        System.out.println();
        if (success)
            System.out.println("-- Program completed successfully in " + getHMS(s) + " --");
        else System.out.println("!! Program completed with errors. !!");

    }

    /**
     * Get Hours:Minutes:Seconds format from number of seconds
     *
     * @param seconds Number of seconds
     * @return HMS formatted String
     */
    public static String getHMS(long seconds) {
        return String.format(
                "%02d:%02d:%02d",
                seconds / 3600,         // Hours
                (seconds % 3600) / 60,  // Minutes
                seconds % 60);          // Seconds
    }

    /**
     * App initialization
     */
    private void init() {
        this.query = new Query();
    }

    /**
     * Main application directive
     *
     * @param verbose Verbosity on/off
     * @return Success of application
     */
    private boolean run(String statement, boolean verbose) {

        boolean success = true;

        setQuery(statement);

        ResultSet resultSet = runQuery();
        Map<String, Set<String>> conceptMap = buildMap(resultSet, verbose);

        success = fetchImages(conceptMap, verbose);

        processImages(verbose);

        return success;
    }

    /**
     * Fetch images from the web given a concept map
     *
     * @param conceptMap Map of concept to image links
     * @param verbose    Verbosity on/off
     * @return boolean Success
     */
    private boolean fetchImages(Map<String, Set<String>> conceptMap, boolean verbose) {

        boolean success = true;

        int totalImages = 0;
        for (String concept : conceptMap.keySet()) totalImages += conceptMap.get(concept).size();

        long begin = System.currentTimeMillis();
        System.out.println("Fetching a total of " + totalImages + " images... ");

        int imageCount = 0;

        for (String concept : conceptMap.keySet()) {

            Iterator<String> setIterator = conceptMap.get(concept).iterator();
            int conceptCount = conceptMap.get(concept).size();

            long last = System.nanoTime();
            System.out.println("Fetching " + conceptCount + " images for concept \"" + concept + "\"");

            for (int i = 0; i < conceptCount; i++) {

                BufferedImage testImage = ImageTools.fetchImage(setIterator.next());
                if (verbose)
                    System.out.print("Fetching \"" + concept + "\" image " + (i + 1) + "/" + conceptCount + "... ");

                try {
                    File imFile = new File("images/" + concept + "_" + (i + 1) + ".png");
                    imFile.mkdirs();
                    ImageIO.write(testImage, "png", imFile);
                    if (verbose) System.out.println("done");
                } catch (IOException e) {
                    if (verbose) System.err.println("failed");
                    success = false;
                    // e.printStackTrace();
                }

                imageCount++;

                if (verbose) {
                    double averageFetchTime = (System.nanoTime() - begin) / 1000000000. / imageCount;
                    long estimatedSecondsRemaining = (long)(averageFetchTime * (totalImages - imageCount));
                    System.out.println("\t\t" + getHMS(estimatedSecondsRemaining) + " remaining");
                }

            }
        }

        return success;

    }

    /**
     * Remove black bars from saved images and store
     *
     * @param verbose Verbosity on/off
     */
    private void processImages(boolean verbose) {

        System.out.println("Removing image borders...");

        File imageFolder = new File("images");
        File[] files = imageFolder.listFiles();
        for (File image : files) {

            if (verbose) System.out.print("Removing borders from " + image.getName() + "... ");

            File croppedImage = new File("cropped_images/" + image.getName().substring(0, image.getName().length() - 4) + "_cropped.png");
            croppedImage.mkdirs();

            try {
                ImageIO.write(ImageTools.removeBlackBars(ImageIO.read(image)), "png", croppedImage);
                if (verbose) System.out.println("done");
            } catch (IOException e) {
                if (verbose) System.err.println("failed");
                // e.printStackTrace();
            }

        }

    }

    /**
     * Build relational concept map from a ResultSet
     *
     * @param resultSet Input ResultSet
     * @param verbose   Verbosity on/off
     * @return Relational concept map
     */
    private Map<String, Set<String>> buildMap(ResultSet resultSet, boolean verbose) {

        Map<String, Set<String>> conceptMap = new HashMap<String, Set<String>>();

        try {

            long last = System.currentTimeMillis();
            if (verbose) System.out.print("Building concept map... ");

            while (resultSet.next()) {

                String conceptName = resultSet.getString(1);
                String imageReference = resultSet.getString(2);
                String imagePng = imageReference.substring(0, imageReference.length() - 3) + "png";

                if (!conceptMap.containsKey(conceptName)) conceptMap.put(conceptName, new HashSet<String>());
                conceptMap.get(conceptName).add(imagePng);

            }

            if (verbose) System.out.println(getHMS((long) ((System.currentTimeMillis() - last) / 1000.)));

        } catch (SQLException e) {
            System.err.println("Error while building concept map. Check SQL results of given query.");
            e.printStackTrace();
        }

        return conceptMap;

    }

    /**
     * Set query statement
     *
     * @param sql String statement
     */
    private void setQuery(String sql) {
        query.setStatement(sql);
    }

    /**
     * Run set query
     *
     * @return ResultSet of query results
     */
    private ResultSet runQuery() {

        long beginQuery = System.currentTimeMillis();
        System.out.print("Beginning query... ");

        ResultSet resultSet = query.executeStatement();

        System.out.println(getHMS((long) ((System.currentTimeMillis() - beginQuery) / 1000.)));

        return resultSet;

    }

}
