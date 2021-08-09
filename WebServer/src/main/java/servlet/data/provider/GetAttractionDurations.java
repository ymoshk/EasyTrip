package servlet.data.provider;

import com.google.gson.Gson;
import constant.DefaultDurations;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;


@WebServlet("/api/getAttractionDurations")
public class GetAttractionDurations extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (PrintWriter out = resp.getWriter()) {
            Gson gson = new Gson();
            Map<String, Integer> durationsMap = DefaultDurations.getAttractionsEST();
            out.println(gson.toJson(durationsMap));
        }
    }
}