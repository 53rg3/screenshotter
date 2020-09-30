package core;

import com.google.gson.Gson;
import utils.Utils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static utils.Utils.exitWithError;
import static utils.Utils.itsABug;

public class CsvRow {

    private final String id;
    private final String url;
    private final List<Object> dataColumns = new ArrayList<>();

    private static final Pattern urlPattern = Pattern.compile("^https?://[^\\s]+$");
    private static final Pattern jsonPattern = Pattern.compile("\\{.*}");
    private static final String expectedCsvStructure = "Expected CSV structure:\n" +
            "1. column: URL\n" +
            "2-9. column: JSON (all optional)";

    public CsvRow(final String[] rowsAsArray) {
        final List<String> row = List.of(rowsAsArray);

        this.url = validateUrl(row.get(0));
        this.id = Utils.createId(row.get(0));

        for (final String json : row.subList(1, row.size())) {
            this.dataColumns.add(validateJson(json));
        }
    }

    private static String validateUrl(final String url) {
        if (!urlPattern.matcher(url).matches()) {
            exitWithError("Provided URL '" + url + "' does not match expected pattern: " + urlPattern + " - got:\n" +
                    expectedCsvStructure);
        }
        return url;
    }

    private static Object validateJson(final String json) {

        final String decodedJson = URLDecoder.decode(json, StandardCharsets.UTF_8);

        if (!jsonPattern.matcher(decodedJson).matches()) {
            exitWithError("Provided JSON doesn't match expected pattern: " + jsonPattern + " - got:\n" + json);
        }

        try {
            return new Gson().fromJson(decodedJson, Object.class);
        } catch (final Exception e) {
            exitWithError("Provided JSON is not valid, exception message: " + e + "\n" + json);
        }
        throw itsABug();
    }

    public String getId() {
        return this.id;
    }

    public String getUrl() {
        return this.url;
    }

    public List<Object> getDataColumns() {
        return this.dataColumns;
    }

}
