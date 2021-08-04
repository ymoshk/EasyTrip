package servlet.itinerary;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/completeItinerary")
public class CompleteItineraryCreation extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        //        Gson gson = new Gson();
        //        HashMap<String, String> data = Utils.parsePostData(req);
        //
        //        String itineraryJson = data.get("itineraryJson");
        //        Itinerary itinerary = gson.fromJson(itineraryJson, Itinerary.class);
        //
        //        ItineraryCache cache = (ItineraryCache) req.getServletContext().getAttribute(Constants.ITINERARY_CACHE);
        //        cache.saveItinerary(itinerary);
    }
}
