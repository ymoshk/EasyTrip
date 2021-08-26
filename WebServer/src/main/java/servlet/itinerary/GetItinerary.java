package servlet.itinerary;

import cache.ItineraryCache;
import com.google.gson.Gson;
import constant.Constants;
import itinerary.Itinerary;
import user.UserContext;
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
        UserContext userContext = (UserContext) Utils.getContext(req).getAttribute(Constants.USERS_CONTEXT);

        HashMap<String, String> data = Utils.parsePostData(req);

        String id = data.get("id");
        res.setStatus(500);
        String sessionId = req.getSession(false).getId();

        if (!id.isEmpty()) {
            Itinerary itinerary = cache.getItinerary(id).orElse(null);

            if (itinerary != null && userContext.isItineraryOwner(sessionId, id)) {
                try (PrintWriter out = res.getWriter()) {
                    res.setStatus(200);
                    out.println(gson.toJson(itinerary));
                }
            }
        }
    }
}

