package core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.ManagingActor.ManagingActorCommand.ProcessScreenshot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static core.HtmlCreator.TemplatePattern.JSON;
import static core.HtmlCreator.TemplatePattern.TITLE;
import static core.RequiredFiles.*;
import static utils.Utils.exitWithError;

public class HtmlCreator {

    private Path outputFolder;
    private Path imageFolder;
    private boolean isInitialized = false;
    private final List<ProcessScreenshot> screenshots = new ArrayList<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public HtmlCreator() {
    }

    public void initialize() {
        this.outputFolder = createPathToOutputFolder();
        this.imageFolder = this.outputFolder.resolve("img/");
        mkdir(this.imageFolder);
        System.out.println("Saving results to " + this.outputFolder);
        this.isInitialized = true;
    }

    public void createOutput() {
        if (!this.isInitialized) {
            exitWithError("Use HtmlCreator.initialize() first.");
        }

        // Create images
        this.screenshots.forEach(this::writeImageFile);

        // Create HTML template
        this.createTemplate();
    }

    private void createTemplate() {
        // Collect data
        final List<CsvRow> list = new ArrayList<>();
        this.screenshots.forEach(screenshot -> list.add(screenshot.csvRow));
        final String json = GSON.toJson(list);

        // Generate template
        String template = fileAsString(templateFile);
        template = this.replaceInTemplate(TITLE, template, this.createTitle());
        template = this.replaceInTemplate(JSON, template, json);
        stringToFile(template, this.outputFolder.resolve(htmlOutputFile));
    }

    private String replaceInTemplate(final TemplatePattern pattern, final String template, final String replacement) {
        return pattern.get().matcher(template).replaceAll(replacement);
    }

    public void addScreenshot(final ProcessScreenshot processScreenshot) {
        this.screenshots.add(processScreenshot);
    }

    public void writeImageFile(final ProcessScreenshot screenshot) {
        try {
            Files.write(this.imageFolder.resolve(screenshot.csvRow.getId() + ".png"),
                    screenshot.image,
                    StandardOpenOption.CREATE);
        } catch (final IOException e) {
            exitWithError("Failed to write image. Exception message: " + e.getMessage());
        }
    }

    public String createTitle() {
        return this.outputFolder.getFileName().toString();
    }

    enum TemplatePattern {
        TITLE(Pattern.compile("#title#")),
        JSON(Pattern.compile("#json#"));

        private final Pattern pattern;

        TemplatePattern(final Pattern pattern) {
            this.pattern = pattern;
        }

        public Pattern get() {
            return this.pattern;
        }
    }
}
