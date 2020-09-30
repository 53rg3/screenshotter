package utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static boolean isInTestMode = false;
    private static final Set<String> uniqueIds = new HashSet<>();
    private static final Pattern idPattern = Pattern.compile("-(\\d+$)");

    public static String createId(final String url) {
        try {
            final URI parsedUrl = new URI(url);
            if (parsedUrl.getHost() == null) {
                exitWithError("Host of provided URL can't be extracted, URL: " + url);
            }

            String id = parsedUrl.getHost();
            while (uniqueIds.contains(id)) {
                id = proposeUniqueId(id);
            }
            uniqueIds.add(id);

            return id;

        } catch (final URISyntaxException e) {
            exitWithError("Malformed url provided: " + e);
        }
        throw itsABug();
    }

    private static String proposeUniqueId(final String id) {
        final Matcher matcher = idPattern.matcher(id);
        if (matcher.find()) {
            try {
                final int nr = Integer.parseInt(matcher.group(1)) + 1;
                return idPattern.matcher(id).replaceAll("-" + nr);
            } catch (final Exception e) {
                exitWithError("Failed to convert suffix in URL to number. Needed to create IDs. Extracted value: " +
                        "" + matcher.group(1) + "\n" +
                        "Host: " + id);
            }
        } else {
            return id + "-2";
        }
        throw itsABug();
    }

    public static IllegalStateException itsABug() {
        return new IllegalStateException("If you see this then it's a bug.");
    }

    public static void exitWithError(final String message) {
        System.err.println(message);
        if (isInTestMode) {
            throw new TestException(message);
        } else {
            System.exit(1);
        }
    }

    public static String timePassedSince(final Instant before) {
        return Instant.now().toEpochMilli() - before.toEpochMilli() + "ms";
    }

    public static class TestException extends RuntimeException {
        public TestException(final String s) {
            super(s);
        }
    }

}
