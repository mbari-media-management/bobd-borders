package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Kevin Barnard
 * @since 2018-06-07T010:00:00
 */
public class Main {

    private static String[] DEFAULT_CONFIG = {
            "SELECT ConceptName, ImageReference FROM QualityImageAnnotations WHERE ImageReference IS NOT NULL AND (" + "" +
                    // Midwater
                    // "ConceptName LIKE 'Apolemia' OR " +
                    // "ConceptName LIKE 'Gonatus' OR " +
                    // "ConceptName LIKE 'Bathochordaeus' OR " +
                    // "ConceptName LIKE 'Solmissus' OR " +
                    // "ConceptName LIKE 'Nanomia' OR " +
                    "ConceptName LIKE 'Aegina' OR " +
                    // Benthic
                    // "ConceptName LIKE 'Chionoecetes' OR " +
                    // "ConceptName LIKE 'Funiculina' OR " +
                    // "ConceptName LIKE 'Pannychia' OR " +
                    // "ConceptName LIKE 'Sebastes' OR " +
                    "ConceptName LIKE 'Sebastolobus' OR " +
                    // Geology
                    // "ConceptName LIKE 'pillow lava' OR " +
                    "ConceptName LIKE 'gravel'" +
            ")",
            "false"
    };

    public static void main(String[] args) {

        establishDriver(); // Call only once

        String[] config = parseArgs(args);

        if (config[0].equals("ERROR")) {
            System.out.println("Error: Incorrect usage of arguments. For help, run program with -? or --help");
            return;
        } else if (config[0].equals("HELP")) {

            File usageFile = new File("src/main/resources/usage.txt");
            try {
                BufferedReader reader = new BufferedReader(new FileReader(usageFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        App mainApp = new App(config);

    }

    /**
     * Parse command-line arguments
     *
     * @param args String array of arguments
     * @return String array of configuration
     */
    private static String[] parseArgs(String[] args) {
        String[] config = DEFAULT_CONFIG;

        String[] error = {"ERROR"};

        for (String arg : args) {

            arg = arg.toLowerCase();

            if (arg.equals("-v") || arg.equals("--verbose")) config[1] = "true";
            else if (arg.equals("-?") || arg.equals("--help")) {
                String[] ret = {"HELP"};
                return ret;
            } else return error;

        }

        return config;
    }

    /**
     * Establish driver and register to DriverManager
     */
    private static void establishDriver() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Microsoft JDBC Server driver not found. Check Maven dependency.");
            e.printStackTrace();
        }
    }

}
