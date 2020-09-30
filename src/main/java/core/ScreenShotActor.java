package core;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import core.ManagingActor.ManagingActorCommand;
import core.ManagingActor.ManagingActorCommand.ProcessScreenshot;
import core.ScreenShotActor.ScreenShotActorCommand;
import core.ScreenShotActor.ScreenShotActorCommand.LoadUrl;
import core.ScreenShotActor.ScreenShotActorCommand.MakeScreenShot;
import org.openqa.selenium.*;
import utils.Utils;

import java.time.Duration;
import java.time.Instant;


public class ScreenShotActor extends AbstractBehavior<ScreenShotActorCommand> {

    private final CsvRow csvRow;
    private final Config config;
    private final WebDriver webDriver;
    private final ActorRef<ManagingActorCommand> managingActor;
    private final Instant startedAt = Instant.now();

    private ScreenShotActor(final ActorContext<ScreenShotActorCommand> context,
                            final CsvRow csvRow,
                            final Config config,
                            final WebDriver webDriver,
                            final ActorRef<ManagingActorCommand> managingActor) {
        super(context);

        this.csvRow = csvRow;
        this.config = config;
        this.webDriver = webDriver;
        this.managingActor = managingActor;
        this.getContext().getSelf().tell(LoadUrl.INSTANCE);
    }

    public static Behavior<ScreenShotActorCommand> create(final CsvRow csvRow,
                                                          final Config config,
                                                          final WebDriver webDriver,
                                                          final ActorRef<ManagingActorCommand> managingActor) {
        return Behaviors.setup(context -> new ScreenShotActor(context, csvRow, config, webDriver, managingActor));
    }


    // ------------------------------------------------------------------------------------------ //
    // RECEIVE
    // ------------------------------------------------------------------------------------------ //

    @Override
    public Receive<ScreenShotActorCommand> createReceive() {
        return this.newReceiveBuilder()
                .onMessage(LoadUrl.class, this::onLoadUrl)
                .onMessage(MakeScreenShot.class, this::onMakeScreenShot)
                .build();
    }


    private Behavior<ScreenShotActorCommand> onMakeScreenShot(final MakeScreenShot unused) {
        final Long height = this.getHeight(this.webDriver);
        this.webDriver.manage().window().setSize(new Dimension(1920, height.intValue()));
        final byte[] bytes = screenshotViaSelenium(this.webDriver);
        this.managingActor.tell(new ProcessScreenshot(this.csvRow, bytes, this.webDriver));
        System.out.println("Done: " + this.csvRow.getUrl() + " - Took: " + Utils.timePassedSince(this.startedAt));

        return Behaviors.stopped();
    }

    private Behavior<ScreenShotActorCommand> onLoadUrl(final LoadUrl unused) {
        this.webDriver.get(this.csvRow.getUrl());

        return Behaviors.withTimers(timerScheduler -> {
            timerScheduler.startSingleTimer(MakeScreenShot.INSTANCE,
                    Duration.ofMillis(this.config.getWaitForRenderingInMs()));
            return this;
        });
    }

    // ------------------------------------------------------------------------------------------ //
    // HELPERS
    // ------------------------------------------------------------------------------------------ //

    private Long getHeight(final WebDriver webDriver) {
        final JavascriptExecutor webDriverJS = (JavascriptExecutor) webDriver;
        return (Long) webDriverJS.executeScript("return document.body.scrollHeight");
    }

    private static byte[] screenshotViaSelenium(final WebDriver webDriver) {
        final TakesScreenshot webDriverShot = ((TakesScreenshot) webDriver);
        return webDriverShot.getScreenshotAs(OutputType.BYTES);
    }

    // todo use this if you need screenshots of pages which load additional content async on scrolling
//    private static void screenshotViaAshot(final WebDriver webDriver) throws IOException {
//        final Screenshot fpScreenshot = new AShot()
//                .coordsProvider(new WebDriverCoordsProvider())
//                .shootingStrategy(ShootingStrategies.viewportPasting(10))
//                .takeScreenshot(webDriver);
//
//        // Easy API
//        ImageIO.write(fpScreenshot.getImage(), "PNG", new File("elementScreenshot.png"));
//    }

    // ------------------------------------------------------------------------------------------ //
    // INNER CLASSES
    // ------------------------------------------------------------------------------------------ //

    public interface ScreenShotActorCommand {

        enum LoadUrl implements ScreenShotActorCommand {INSTANCE}

        enum MakeScreenShot implements ScreenShotActorCommand {INSTANCE}

    }


}
