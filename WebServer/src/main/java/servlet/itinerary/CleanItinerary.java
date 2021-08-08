package servlet.itinerary;

import cache.ItineraryCache;
import constant.Constants;
import itinerary.Itinerary;
import itinerary.ItineraryDay;
import util.Utils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@WebServlet("/api/cleanItinerary")
public class CleanItinerary extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        ItineraryCache cache = (ItineraryCache) Utils.getContext(req).getAttribute(Constants.ITINERARY_CACHE);

        HashMap<String, String> data = Utils.parsePostData(req);

        String id = data.get("id");

        if (!id.isEmpty()) {
            Itinerary itineraryToEdit = cache.getItinerary(id);

            if (itineraryToEdit != null) {
                itineraryToEdit.getItineraryDays().forEach(ItineraryDay::clean);
            }
        }
    }
}