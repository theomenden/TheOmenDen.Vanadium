package vanadium.utils;

import vanadium.VanadiumClient;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public final class LoggerUtils {
    private LoggerUtils() {
    }

    public static Logger getLoggerInstance() {
        var logger = Logger.getLogger(VanadiumClient.MODID);
        var handler = new ConsoleHandler();
        handler.setFormatter(new LogFormat());
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
        return logger;
    }

    private static class LogFormat extends Formatter {

        private static final DateTimeFormatter TimingFormat = DateTimeFormatter.ofPattern("HH:mm:ss");



        @Override
        public String format(LogRecord record) {
            var currentZonedTime = ZonedDateTime.ofInstant(record.getInstant(), ZoneId.systemDefault());
            var timings = "[" + TimingFormat.format(currentZonedTime) +"] \u001B[32m";
            var recordingInformation = "[" + VanadiumClient.MODID + "/" + record.getLevel().getLocalizedName() + "]\u001B[36m (" + record.getSourceClassName() + "): \u001B[0m";
            var message = record.getMessage() + "\n";

            return new StringBuilder()
                    .append("\u001B[34m")
                    .append(timings)
                    .append(recordingInformation)
                    .append(message)
                    .toString();
        }
    }
}
