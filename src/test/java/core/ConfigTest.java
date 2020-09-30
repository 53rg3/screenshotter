package core;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConfigTest {

    @Test
    public void loadConfig() {
        final Config config = Config.load();

        final String propertyKey = "webdriver.chrome.driver";
        assertThat(config.getWebDriverName(), is("webdriver.chrome.driver"));
        assertThat(config.getWebDriverFile().endsWith("/assets/chromedriver"), is(true));

        assertThat("Expected system property to point to ./assets/chromedriver, got: " + System.getProperty(propertyKey),
                System.getProperty(propertyKey).endsWith("/assets/chromedriver"), is(true));
    }

}
