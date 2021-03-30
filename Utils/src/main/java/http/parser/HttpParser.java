//package http.parser;
//
//import log.LogsManager;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//
//import java.io.IOException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Hashtable;
//import java.util.List;
//import java.util.Map;
//
//
//public class HttpParser {
//
//    private final List<String> uriParts = new ArrayList<>();
//    private final Map<String, String> params = new Hashtable<>();
//    private String url;
//
//    public HttpParser(String url) {
//        this.url = url;
//    }
//
//    public void addParam(String key, String value) {
//        this.params.put(key, value);
//    }
//
//    public void changeParam(String key, String newValue) {
//        addParam(key, newValue);
//    }
//
//    public void addUriPart(String part) {
//        this.uriParts.add(part);
//    }
//
//    private void buildUrl() {
//        StringBuilder stringBuilder = new StringBuilder(this.url);
//
//        for (String part : this.uriParts) {
//            stringBuilder.append('/').append(part);
//        }
//
//        stringBuilder.append('/');
//
//        if (!params.isEmpty()) {
//            stringBuilder.append("?");
//        }
//
//        for (Map.Entry<String, String> param : this.params.entrySet()) {
//            stringBuilder.append(param.getKey()).append('=').append(param.getValue()).append('&');
//        }
//
//        // Remove the unnecessary '&' if the params map isn't empty.
//        if (!params.isEmpty()) {
//            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
//        }
//
//        this.url = stringBuilder.toString();
//    }
//
//    //    private String getHtml() {
//    //        HttpRequest request = new BasicHttpRequest();
//    //        StringBuilder html = new StringBuilder();
//    //
//    //        try {
//    //            HttpResponse response = request.doGetRequest(new URL(this.url));
//    //
//    //            if (response.getStatusCode() == 200) {
//    //                try (Scanner scanner = new Scanner(response.getInputStream())) {
//    //                    System.out.println(scanner.);
//    ////                    while (scanner.hasNextLine()) {
//    ////                        html.append(scanner);
//    ////                    }
//    //                }
//    //            }
//    //
//    //        } catch (IOException e) {
//    //            LogsManager.logException(e);
//    //        }
//    //
//    //        return html.toString();
//    //    }
//
//    public Document parse() {
//        Document result = null;
//        buildUrl();
//
//        try {
//            result = Jsoup.parse(new URL(this.url), 1000 * 60);
//        } catch (IOException e) {
//            LogsManager.logException(e);
//        }
//
//        return result;
//    }
//}
