package core;

import org.junit.BeforeClass;
import org.junit.Test;
import utils.Utils;
import utils.Utils.TestException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CsvLoaderTest {

    @BeforeClass
    public static void before() {
        Utils.isInTestMode = true;
    }

    @Test
    public void cannot_find_file() {
        try {
            new CsvLoader("does_not_exist");
        } catch (final Exception e) {
            this.assertTestException("Expected input CSV file not found", e);
        }
    }

    @Test
    public void valid() {
        new CsvLoader("src/test/resources/test.csv");
    }

    @Test
    public void invalid_url() {
        try {
            new CsvLoader("src/test/resources/test-invalid-url.csv");
        } catch (final Exception e) {
            this.assertTestException("Provided URL", e);
        }
    }

    @Test
    public void invalid_json_1() {
        try {
            new CsvLoader("src/test/resources/test-invalid-json-1.csv");
        } catch (final Exception e) {
            this.assertTestException("Provided JSON", e);
        }
    }

    @Test
    public void invalid_json_2() {
        try {
            new CsvLoader("src/test/resources/test-invalid-json-2.csv");
        } catch (final Exception e) {
            this.assertTestException("Provided JSON", e);
        }
    }


    private void assertTestException(final String expectedMessage, final Exception e) {
        assertThat("Expected TestException.class",
                e instanceof TestException, is(true));
        assertThat(e.getMessage().contains(expectedMessage), is(true));
    }

}
