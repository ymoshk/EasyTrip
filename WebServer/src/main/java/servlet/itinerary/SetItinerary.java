package servlet.itinerary;

import cache.ItineraryCache;
import com.google.gson.Gson;
import constant.Constants;
import itinerary.Itinerary;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet("/api/setItinerary")
public class SetItinerary extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Gson gson = new Gson();
        String itineraryJson = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Itinerary itinerary = gson.fromJson(itineraryJson, Itinerary.class);

        ItineraryCache cache = (ItineraryCache) req.getServletContext().getAttribute(Constants.ITINERARY_CACHE);

        if (cache != null) {
            cache.addNewItinerary(itinerary);
        } else {
            res.setStatus(500);
        }
    }
}
