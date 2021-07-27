package servlet.data.provider;

import com.google.gson.Gson;
import data.tag.TagsList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/getTripTags")
public class TripTagsProvider extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();

        try (PrintWriter out = resp.getWriter()) {
            System.out.println(gson.toJson(TagsList.getAll()));
            out.println(gson.toJson(TagsList.getAll()));
        }
    }
}
