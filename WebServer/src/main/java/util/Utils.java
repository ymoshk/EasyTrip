package util;

import com.google.gson.Gson;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    public static String getSessionId(HttpServletRequest req, HttpServletResponse response) {
        Cookie[] cookies = req.getCookies();
        Cookie cookie;

        if (cookies == null) {
            cookies = new Cookie[0];
        }

        List<Cookie> cookieList = Arrays.asList(cookies);

        if (cookieList.stream().noneMatch(cook -> cook.getName().equals("ET_SESSION"))) {
            cookie = new Cookie(
                    "ET_SESSION", req.getSession().getId());

            cookie.setMaxAge(60 * 60 * 24 * 7);
            cookie.setPath("/");
            response.addCookie(cookie);
        } else {
            cookie = cookieList.stream()
                    .filter(cooki -> cooki.getName().equals("ET_SESSION"))
                    .findFirst()
                    .get();
        }

        return cookie.getValue();
    }
}
