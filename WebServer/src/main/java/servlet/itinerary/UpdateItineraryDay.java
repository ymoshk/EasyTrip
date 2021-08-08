package servlet.itinerary;

import cache.ItineraryCache;
import com.google.gson.Gson;
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

@WebServlet("/api/updateItinerary")
public class UpdateItineraryDay extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Gson gson = new Gson();
        ItineraryCache cache = (ItineraryCache) Utils.getContext(req).getAttribute(Constants.ITINERARY_CACHE);

        HashMap<String, String> data = Utils.parsePostData(req);

        String id = data.get("id");
        String dayJson = data.get("dayJson");
        String dayIndex = data.get("index");

        if (!id.isEmpty() && !dayJson.isEmpty() && !dayIndex.isEmpty()) {
            Itinerary itineraryToEdit = cache.getItinerary(id);

            if (itineraryToEdit != null) {
                ItineraryDay itineraryDay = gson.fromJson(dayJson, ItineraryDay.class);
                itineraryToEdit.setItineraryDay(Integer.parseInt(dayIndex), itineraryDay);
            }
        }
    }
}