package core;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static core.RequiredFiles.exitIfFileDoesNotExist;
import static utils.Utils.exitWithError;

/**
 * - Loads all CSV lines at once.
 * - Validates all lines before making them available
 */
public class CsvLoader {

    private final Path csvPath;
    private final CSVParser parser = new CSVParserBuilder()
            .withSeparator('\t')
            .withIgnoreQuotations(true)
            .build();
    private final List<CsvRow> csvRows = new ArrayList<>();

    public CsvLoader(final String csvPath) {
        this.csvPath = RequiredFiles.resolveToRoot(csvPath);
        exitIfFileDoesNotExist(this.csvPath, "Expected input CSV file not found at: " + this.csvPath);
        this.load();
    }

    private void load() {
        try {
            final List<String> lines = Files.readAllLines(this.csvPath);

            for (final String line : lines) {
                this.csvRows.add(new CsvRow(this.parser.parseLine(line))); // todo
            }

        } catch (final IOException e) {
            exitWithError("Failed to read CSV file at " + this.csvPath + " - Reason: \n" + e.getMessage());
        }
    }

    private File fileInRootPath(final String csvPath) {
        return new File("").toPath().resolve(csvPath).toFile();
    }

    public List<CsvRow> getCsvRows() {
        return this.csvRows;
    }
}
