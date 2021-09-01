package servlet.itinerary;

import cache.ItineraryCache;
import constant.Constants;
import util.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@WebServlet("/api/deleteItinerary")
public class DeleteItinerary extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ItineraryCache cache = (ItineraryCache) Utils.getContext(req).getAttribute(Constants.ITINERARY_CACHE);
        HashMap<String, String> data = Utils.parsePostData(req);
        String id = data.get("id");
        cache.removeItinerary(id);
    }
}
