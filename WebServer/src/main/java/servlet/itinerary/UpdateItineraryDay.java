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
import java.util.Optional;

@WebServlet("/api/updateItinerary")
public class UpdateItineraryDay extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Gson gson = new Gson();
        ItineraryCache cache = (ItineraryCache) Utils.getContext(req).getAttribute(Constants.ITINERARY_CACHE);
        res.setStatus(500);
        HashMap<String, String> data = Utils.parsePostData(req);

        String id = data.get("id");
        String dayJson = data.get("dayJson");
        String dayIndex = data.get("index");

        if (!id.isEmpty() && !dayJson.isEmpty() && !dayIndex.isEmpty()) {
            Optional<Itinerary> itineraryToEdit = cache.getItinerary(id);

            itineraryToEdit.ifPresent(itinerary -> {
                ItineraryDay itineraryDay = gson.fromJson(dayJson, ItineraryDay.class);
                itinerary.setItineraryDay(Integer.parseInt(dayIndex), itineraryDay);
                res.setStatus(200);
            });
        }
    }
}