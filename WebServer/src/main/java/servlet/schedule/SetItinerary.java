package servlet.schedule;

import com.google.gson.Gson;
import util.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Collectors;

@WebServlet("/api/setItinerary")
public class SetItinerary extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String Itinerary = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        System.out.println(Itinerary);
    }
}
