package util;

import com.google.gson.Gson;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class Utils {
    public static HashMap<String, String> parsePostData(HttpServletRequest req) throws IOException {
        HashMap<String, String> result = new HashMap<>();
        HashMap<String, String> temp;
        Gson gson = new Gson();
        try (BufferedReader reader = req.getReader()) {
            temp = gson.fromJson(reader, HashMap.class);
        }

        temp.forEach((key, value) -> result.put(key, value.trim()));

        return result;
    }

    public static ServletContext getContext(HttpServletRequest req) {
        return req.getServletContext();
    }
}
