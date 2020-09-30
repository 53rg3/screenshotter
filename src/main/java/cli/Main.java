package cli;

import akka.actor.typed.ActorSystem;
import core.Config;
import core.CsvLoader;
import core.ManagingActor;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import static core.RequiredFiles.defaultInputFile;
import static utils.Utils.exitWithError;

@Command(description = "\n" +
        "Screenshots.\n\n" +
        "Options:",
        name = "java -jar pdd.jar", mixinStandardHelpOptions = true, version = "0.1")
public class Main implements Callable<Integer> {

    @Parameters(index = "0",
            description = "path to input CSV. 1. Column URL, 2-x. JSON columns",
            defaultValue = defaultInputFile)
    private String inputFile;

    public static void main(final String[] args) {
        final int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {

        // Load data and validate
        final Config config = Config.load();
        final CsvLoader csvLoader = new CsvLoader(this.inputFile);
        System.out.println("Total URLs: " + csvLoader.getCsvRows().size());

        // Start ActorSystem
        ActorSystem.create(ManagingActor.create(config, csvLoader), "ManagingActor");

        // Prevent application from stopping
        try {
            Thread.currentThread().join();
        } catch (final InterruptedException e) {
            exitWithError("Something went wrong: " + e.getMessage());
        }

        return 0;
    }
}
