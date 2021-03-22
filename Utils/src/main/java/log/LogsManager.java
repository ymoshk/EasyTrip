package log;

public class LogsManager {

    private final static boolean debugMode = true;

    public static void logException(Exception ex) {
        if (debugMode) {
            ex.printStackTrace();
        }
    }
}
