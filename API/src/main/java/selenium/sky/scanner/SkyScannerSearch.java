package selenium.sky.scanner;

import selenium.ChromeBrowser;

import java.io.Closeable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public abstract class SkyScannerSearch implements Closeable {

    protected final ChromeBrowser browser = new ChromeBrowser();
    private final List<String> uriParts = new ArrayList<>();
    private final SortedMap<String, String> params = new TreeMap<>();
    private final String url = "https://www.skyscanner.com/transport/flights";

    @Override
    public void close() {
        this.browser.close();
    }

    protected void addParam(String key, String value) {
        this.params.put(key, value);
    }

    protected void changeParam(String key, String newValue) {
        addParam(key, newValue);
    }

    protected void addUriPart(String part) {
        this.uriParts.add(part);
    }

    protected String getUrl() {
        StringBuilder stringBuilder = new StringBuilder(this.url);

        for (String part : this.uriParts) {
            stringBuilder.append('/').append(part);
        }

        stringBuilder.append('/');

        if (!params.isEmpty()) {
            stringBuilder.append("?");
        }

        for (Map.Entry<String, String> param : this.params.entrySet()) {
            stringBuilder.append(param.getKey()).append('=').append(param.getValue()).append('&');
        }

        // Remove the unnecessary '&' if the params map isn't empty.
        if (!params.isEmpty()) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        return stringBuilder.toString();
    }

    protected String convertDateToFormat(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("yyMMdd"));
    }
}
