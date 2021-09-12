package servlet.itinerary;

import algorithm.HillClimbing;
import cache.ItineraryCache;
import com.google.gson.Gson;
import connection.DataEngine;
import constant.Constants;
import container.PriceRange;
import itinerary.Itinerary;
import itinerary.ItineraryBuilderUtil;
import model.attraction.Attraction;
import model.itinerary.ItineraryStatus;
import model.user.User;
import user.UserContext;
import util.Utils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/completeQuestionsAuto")
public class QuestionsCompletedAuto extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ItineraryBuilderUtil itineraryBuilder = new ItineraryBuilderUtil(Utils.parsePostData(req), true);
        UserContext userContext = (UserContext) req.getServletContext().getAttribute(Constants.USERS_CONTEXT);
        User user = userContext.getUserBySessionId(Utils.getSessionId(req, resp));
        resp.setStatus(500);

        try (PrintWriter out = resp.getWriter()) {
            Itinerary itinerary = itineraryBuilder.getItinerary();
            ItineraryCache cache = (ItineraryCache) req.getServletContext().getAttribute(Constants.ITINERARY_CACHE);
            cache.addNewItinerary(itinerary, user, true);

            Gson gson = new Gson();
            out.println(gson.toJson(itinerary.getItineraryId()));
            resp.setStatus(200);
            Thread thread = new Thread(() -> threadUtil(itinerary, itineraryBuilder, cache));
            thread.start();
        } catch (Exception ignore) {
        }
    }

    private void threadUtil(Itinerary itinerary, ItineraryBuilderUtil itineraryBuilder, ItineraryCache cache) {
        DataEngine dataEngine = DataEngine.getInstance();
        itineraryBuilder.fillAttraction();
        //shouldFetchAttraction = false since we don't want to fetch new attractions
        List<Attraction> attractionList = dataEngine.getAttractions(itinerary.getQuestionsData().getCity().getCityName(),
                new PriceRange(2), false);
        HillClimbing hillClimbing = new HillClimbing(itinerary.getQuestionsData(), attractionList);

        // build itinerary
        itinerary.addOutboundToItinerary();
        hillClimbing.getBestItinerary(itinerary);
        itinerary.addReturnToItinerary();

        itinerary.setAttractions(itineraryBuilder.getAttractions());
        itinerary.fixTransportationNodes();

        cache.updateItinerary(itinerary);
        cache.updateItineraryStatus(itinerary.getItineraryId(), ItineraryStatus.COMPLETED);
    }
}