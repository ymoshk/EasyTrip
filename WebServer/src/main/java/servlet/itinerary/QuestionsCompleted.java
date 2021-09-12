package servlet.itinerary;

import cache.ItineraryCache;
import com.google.gson.Gson;
import constant.Constants;
import itinerary.Itinerary;
import itinerary.ItineraryBuilderUtil;
import model.user.User;
import user.UserContext;
import util.Utils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/completeQuestions")
public class QuestionsCompleted extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserContext userContext = (UserContext) req.getServletContext().getAttribute(Constants.USERS_CONTEXT);
        User user = userContext.getUserBySessionId(Utils.getSessionId(req, resp));
        resp.setStatus(500);

        try (PrintWriter out = resp.getWriter()) {
            ItineraryBuilderUtil itineraryBuilder = new ItineraryBuilderUtil(Utils.parsePostData(req));
            Itinerary itinerary = itineraryBuilder.getItinerary();
            itinerary.addOutboundToItinerary();
            itinerary.addReturnToItinerary();
            Gson gson = new Gson();

            if (itinerary != null) {
                ItineraryCache cache = (ItineraryCache) req.getServletContext()
                        .getAttribute(Constants.ITINERARY_CACHE);

                cache.addNewItinerary(itinerary, user, false);
                out.println(gson.toJson(itinerary.getItineraryId()));
            }
            resp.setStatus(200);
        } catch (IOException ignore) {
        }
    }
}

