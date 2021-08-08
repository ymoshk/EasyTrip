package servlet.itinerary;

import cache.ItineraryCache;
import com.google.gson.Gson;
import constant.Constants;
import itinerary.Itinerary;
import util.Utils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

@WebServlet("/api/getItinerary")
public class GetItinerary extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Gson gson = new Gson();
        ItineraryCache cache = (ItineraryCache) Utils.getContext(req).getAttribute(Constants.ITINERARY_CACHE);

        HashMap<String, String> data = Utils.parsePostData(req);

        String id = data.get("id");

        if (!id.isEmpty()) {
            Itinerary result = cache.getItinerary(id);

            try (PrintWriter out = res.getWriter()) {
                if (result != null) {
                    out.println(gson.toJson(result));
                }
            }
        }
    }
}