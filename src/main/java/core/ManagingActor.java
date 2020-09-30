package core;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import core.ManagingActor.ManagingActorCommand;
import core.ManagingActor.ManagingActorCommand.ProcessScreenshot;
import core.ManagingActor.ManagingActorCommand.Start;
import org.openqa.selenium.WebDriver;
import utils.Utils;

import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;


public class ManagingActor extends AbstractBehavior<ManagingActorCommand> {

    private final Config config;
    private final CsvLoader csvLoader;
    private final HtmlCreator htmlCreator = new HtmlCreator();
    private final Queue<WebDriver> webDrivers = new LinkedList<>();
    private final Queue<CsvRow> csvRows = new LinkedList<>();
    private final Instant startedAt = Instant.now();

    private ManagingActor(final ActorContext<ManagingActorCommand> context,
                          final Config config,
                          final CsvLoader csvLoader) {
        super(context);

        this.config = config;
        this.csvLoader = csvLoader;
        this.getContext().getSelf().tell(Start.INSTANCE);
    }

    public static Behavior<ManagingActorCommand> create(final Config config,
                                                        final CsvLoader csvLoader) {
        return Behaviors.setup(context -> new ManagingActor(context, config, csvLoader));
    }


    // ------------------------------------------------------------------------------------------ //
    // RECEIVE
    // ------------------------------------------------------------------------------------------ //

    @Override
    public Receive<ManagingActorCommand> createReceive() {
        return this.newReceiveBuilder()
                .onMessage(Start.class, this::onStart)
                .onMessage(ProcessScreenshot.class, this::onProcessScreenShot)
                .build();
    }


    private Behavior<ManagingActorCommand> onStart(final Start msg) {
        // Create WebDrivers
        IntStream.rangeClosed(1, this.config.getThreads()).forEach(val ->
                this.webDrivers.add(WebDriverFactory.chromeWebDriver()));

        // Create CsvRows
        this.csvRows.addAll(this.csvLoader.getCsvRows());

        this.startNewTasks();

        return this;
    }

    private Behavior<ManagingActorCommand> onProcessScreenShot(final ProcessScreenshot msg) {
        this.htmlCreator.addScreenshot(msg);
        this.webDrivers.add(msg.webDriver);
        this.startNewTasks();
        this.checkIfDone();
        return this;
    }

    // ------------------------------------------------------------------------------------------ //
    // HELPERS
    // ------------------------------------------------------------------------------------------ //

    private void startNewTasks() {
        if (this.csvRows.isEmpty()) {
            return;
        }

        if (this.webDrivers.isEmpty()) {
            return; // wait for next task to refill queue
        }

        WebDriver webDriver = this.webDrivers.poll();
        while (webDriver != null) {

            this.getContext().spawn(
                    ScreenShotActor.create(this.csvRows.poll(), this.config, webDriver, this.getContext().getSelf()),
                    UUID.randomUUID().toString());

            webDriver = this.webDrivers.poll();
        }
    }

    // ------------------------------------------------------------------------------------------ //
    // HELPERS
    // ------------------------------------------------------------------------------------------ //

    private void checkIfDone() {
        if (this.csvRows.isEmpty() && this.webDrivers.size() == this.config.getThreads()) {
            this.shutdownResources();
            this.createOutput();
            System.exit(0);
        }
    }

    private void shutdownResources() {
        System.out.println("All done. Shutting down WebDrivers. Total time: " + Utils.timePassedSince(this.startedAt));
        this.webDrivers.forEach(WebDriver::quit);
    }

    private void createOutput() {
        this.htmlCreator.initialize();
        this.htmlCreator.createOutput();
    }

    // ------------------------------------------------------------------------------------------ //
    // INNER CLASSES
    // ------------------------------------------------------------------------------------------ //

    public interface ManagingActorCommand {

        enum Start implements ManagingActorCommand {INSTANCE}

        class ProcessScreenshot implements ManagingActorCommand {
            final CsvRow csvRow;
            final byte[] image;
            final WebDriver webDriver;

            public ProcessScreenshot(final CsvRow csvRow, final byte[] image, final WebDriver webDriver) {
                this.csvRow = csvRow;
                this.image = image;
                this.webDriver = webDriver;
            }
        }

    }


}
