package core;

import com.google.gson.Gson;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static core.RequiredFiles.configFilePath;
import static core.RequiredFiles.exitIfFileDoesNotExist;
import static utils.Utils.exitWithError;
import static utils.Utils.itsABug;

public class Config {
    private Config() {
    }

    @NotNull
    @NotEmpty
    private String webDriverName;

    @NotNull
    @NotEmpty
    private String webDriverFile;

    @Min(value = 1)
    private int threads;

    @Min(value = 0)
    private int waitForRenderingInMs;


    // ------------------------------------------------------------------------------------------ //
    // HELPERS
    // ------------------------------------------------------------------------------------------ //

    public static Config load() {
        final Config config = configJsonToObject();
        initialize(config);
        return config;
    }

    private static void initialize(final Config config) {
        exitIfFileDoesNotExist(Paths.get(config.webDriverFile),
                "Can't find WebDriver file at: " + config.webDriverFile);

        System.setProperty(config.webDriverName, config.webDriverFile);
    }

    private static Config configJsonToObject() {
        Config config = new Config();
        try {
            config = new Gson().fromJson(configAsString(), Config.class);
        } catch (final Exception e) {
            exitWithError("JSON of config file is invalid. Exception message:\n" + e.getMessage());
        }

        final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        final Set<ConstraintViolation<Config>> violations = validator.validate(config);
        if (!violations.isEmpty()) {
            violations.forEach(violation -> {
                System.err.println("Field '" + violation.getPropertyPath() + "' " + violation.getMessage());
            });
            exitWithError("config.json is invalid.");
        }
        return config;
    }

    private static String configAsString() {
        exitIfFileDoesNotExist(configFilePath);

        try {
            return new String(Files.readAllBytes(configFilePath));
        } catch (final IOException e) {
            exitWithError("Failed to load config file at " + configFilePath);
        }
        throw itsABug();
    }


    // ------------------------------------------------------------------------------------------ //
    // GETTERS
    // ------------------------------------------------------------------------------------------ //

    public String getWebDriverName() {
        return this.webDriverName;
    }

    public String getWebDriverFile() {
        return this.webDriverFile;
    }

    public int getThreads() {
        return this.threads;
    }

    public int getWaitForRenderingInMs() {
        return this.waitForRenderingInMs;
    }
}
