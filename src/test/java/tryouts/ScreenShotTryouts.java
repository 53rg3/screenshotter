package tryouts;

import core.Config;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

public class ScreenShotTryouts {

    public static void main(final String[] args) throws Exception {
        final Config config = Config.load();
        final ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");
        options.addArguments("--disable-extensions");
//        chromeOptions.addArguments("user-agent=SomeUserA gent"); // The arguments to use when starting Chrome.
//        chromeOptions.addArguments("headless"); // The arguments to use when starting Chrome.

        // NOT REUSED
//        Instant now = Instant.now();
//        final WebDriver webDriver1 = new ChromeDriver(chromeOptions);
//        webDriver1.get("https://www.google.com/");
//        System.out.println(getTimePassed(now));
//
//        now = Instant.now();
//        final WebDriver webDriver2 = new ChromeDriver(chromeOptions);
//        webDriver2.get("https://www.bing.com/");
//        System.out.println(getTimePassed(now));
//
//        now = Instant.now();
//        final WebDriver webDriver3 = new ChromeDriver(chromeOptions);
//        webDriver3.get("https://www.bing.com/");
//        System.out.println(getTimePassed(now));

        // REUSED
        Instant now = Instant.now();
        final WebDriver webDriver = new ChromeDriver(options);
        webDriver.manage().window().setSize(new Dimension(1920, 1080)); // todo how high?

//        webDriver1.get("https://www.google.com/");
//        System.out.println(getTimePassed(now));
//
//        now = Instant.now();
//        webDriver1.get("https://www.bing.com/");
//        System.out.println(getTimePassed(now));

        now = Instant.now();
        final Instant total = now;
        webDriver.get("https://www.otto.de/");
        Thread.sleep(500);

        // Adjust Viewport size
        final JavascriptExecutor webDriverJS = (JavascriptExecutor) webDriver;
        final Long height = (Long) webDriverJS.executeScript("return document.body.scrollHeight");
        webDriver.manage().window().setSize(new Dimension(1920, height.intValue())); // todo increase afterwards works
        System.out.println("Request: " + getTimePassed(now));

        now = Instant.now();
//        screenshotViaSelenium(webDriver);
        screenshotViaAshot(webDriver);
        System.out.println("Screenshot: " + getTimePassed(now));

        // Ashot
//        now = Instant.now();
//        takeFullPageScreenShotAsByte(webDriver);
//        System.out.println("Screenshot: " + getTimePassed(now));

        System.out.println("Total time: " + getTimePassed(total));
        webDriver.quit();
    }

    private static String getTimePassed(final Instant before) {
        return Instant.now().toEpochMilli() - before.toEpochMilli() + "ms";
    }

    private static void screenshotViaSelenium(final WebDriver webDriver) throws Exception {
        final TakesScreenshot webDriverShot = ((TakesScreenshot) webDriver);
        final byte[] bytes = webDriverShot.getScreenshotAs(OutputType.BYTES);
        Files.write(new File("").toPath().resolve("screenshot.png"), bytes, StandardOpenOption.CREATE);
    }

    /**
     * https://github.com/pazone/ashot
     */
    private static void screenshotViaAshot(final WebDriver webDriver) throws IOException {
        final Screenshot fpScreenshot = new AShot()
                .coordsProvider(new WebDriverCoordsProvider())
                .shootingStrategy(ShootingStrategies.viewportPasting(10))
                .takeScreenshot(webDriver);

        // Easy API
        ImageIO.write(fpScreenshot.getImage(), "PNG", new File("elementScreenshot.png")); // todo use this. Seems faster.

        // With OutputStream
//        final BufferedImage originalImage = fpScreenshot.getImage();
//        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//            ImageIO.write(originalImage, "png", baos);
//            baos.flush();
//            System.out.println("Write: " + getTimePassed(now));
//            Files.write(new File("").toPath().resolve("screenshot.png"), baos.toByteArray(), StandardOpenOption.CREATE);
//        }

    }

}
