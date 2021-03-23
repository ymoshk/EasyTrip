package log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LogsManager {

    private final static boolean debugMode = true;
    private final static boolean saveToFiles = true;
    private final static String lineSeparator =
            "--------------------------------------------------------------------";

    public static void logException(Exception ex) {
        if (debugMode) {
            ex.printStackTrace();
        }
        if (saveToFiles) {
            saveToFile(ex.getMessage(), logType.exception);
        }
    }

    public static void log(String message) {
        if (debugMode) {
            System.out.println(message);
        }
        if (saveToFiles) {
            saveToFile(message, logType.log);
        }
    }

    private static void saveToFile(String message, logType type) {
        LocalDate currentDate = LocalDate.now();
        String dateString = currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String fName;

        if (type == logType.log) {
            fName = "simpleLogs/logs_" + dateString + ".log";
        } else {
            fName = "exceptions/exceptions_" + dateString + ".log";
        }

        try {
            File file = new File("Utils/src/main/resources/logs/" + fName);

            if (file.exists()) {
                try (FileWriter fileWriter = new FileWriter(file, true)) {
                    fileWriter.write(buildMessage(message));
                } catch (FileNotFoundException ignored) {
                }
            } else {
                if (file.createNewFile()) {
                    try (PrintWriter writer = new PrintWriter(file)) {
                        writer.print(buildMessage(message));
                    } catch (FileNotFoundException ignored) {
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static String buildMessage(String message) {
        LocalTime time = LocalTime.now();
        String timeString = time.format(DateTimeFormatter.ofPattern("hh:mm:ss"));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Time: ").append(timeString).append('\n');
        stringBuilder.append("Message: ").append(message).append('\n');
        stringBuilder.append(lineSeparator).append(lineSeparator)
                .append('\n');
        return stringBuilder.toString();
    }

    private enum logType {
        exception,
        log
    }
}
