package core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static utils.Utils.exitWithError;
import static utils.Utils.itsABug;

/**
 * One place for required files.
 * todo put other places in here
 */
public class RequiredFiles {

    // Base
    static final Path ROOT_FOLDER = Paths.get(new File("").getAbsolutePath());
    static final Path ASSETS_FOLDER = ROOT_FOLDER.resolve("assets/");

    // Required files
    public static final String defaultInputFile = "input.csv"; // Must be string because default CLI param
    public static final String htmlOutputFile = "index.html"; // Must be string because default CLI param
    static final Path configFilePath = ASSETS_FOLDER.resolve("config.json");
    static final Path templateFile = ASSETS_FOLDER.resolve("template.html");
    // todo jquery
    // todo draggable modal

    static {
        exitIfFileDoesNotExist(templateFile);
    }

    // Other
    private static final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd-hh:mm:ss")
            .withZone(ZoneId.systemDefault());


    public static void exitIfFileDoesNotExist(final Path path, final String exitErrorMessage) {
        if (!path.toFile().exists()) {
            exitWithError(exitErrorMessage);
        }
    }

    public static void exitIfFileDoesNotExist(final Path path) {
        if (!path.toFile().exists()) {
            exitWithError("Required file wasn't found at: " + path);
        }
    }

    public static Path resolveToRoot(final String path) {
        return ROOT_FOLDER.resolve(path);
    }

    public static Path createPathToOutputFolder() {
        final String outputFolder = new File("output/").getAbsolutePath();
        final Path path = Paths.get(outputFolder).resolve(formatter.format(Instant.now()));
        createOutputFolder(path);
        return path;
    }

    private static void createOutputFolder(final Path path) {
        final Path outputFolder = path.getParent();
        if (!outputFolder.toFile().exists()) {
            mkdir(outputFolder);
        }

        mkdir(path);
    }

    public static void mkdir(final Path directory) {
        if (!directory.toFile().mkdir()) {
            exitWithError("Failed to create folder at: " + directory.toAbsolutePath());
        }
    }

    public static String fileAsString(final Path path) {
        try {
            return new String(Files.readAllBytes(path));
        } catch (final IOException e) {
            exitWithError("Failed to load file: " + path);
        }
        throw itsABug();
    }

    public static void stringToFile(final String file, final Path path) {
        try {
            Files.write(path, file.getBytes(), StandardOpenOption.CREATE);
        } catch (final IOException e) {
            exitWithError("Failed to write file: " + path);
        }
    }


}
