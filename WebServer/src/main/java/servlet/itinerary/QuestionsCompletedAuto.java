package servlet.itinerary;

import algorithm.HillClimbing;
import algorithm.State;
import cache.ItineraryCache;
import com.google.gson.Gson;
import constant.Constants;
import itinerary.Itinerary;
import itinerary.ItineraryBuilderUtil;
import itinerary.QuestionsData;
import model.attraction.Attraction;
import model.user.User;
import user.UserContext;
import util.Utils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@WebServlet("/api/completeQuestionsAuto")
public class QuestionsCompletedAuto extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ItineraryBuilderUtil itineraryBuilder = new ItineraryBuilderUtil(Utils.parsePostData(req));
        UserContext userContext = (UserContext) req.getServletContext().getAttribute(Constants.USERS_CONTEXT);
        Optional<User> maybeUser = userContext.getLoggedInUser(req.getSession(false).getId());
        resp.setStatus(500);

        try (PrintWriter out = resp.getWriter()) {
            maybeUser.ifPresent(user -> {
                QuestionsData questionsData = itineraryBuilder.getQuestionsData();
                List<Attraction> attractionList = questionsData.getCity().getAttractionList();
                HillClimbing hillClimbing = new HillClimbing(questionsData, attractionList);
                State state = new State(new Itinerary(new HashMap<>(), questionsData), 0.0);
                Itinerary itinerary = hillClimbing.getItineraryWithHillClimbingAlgorithm(state);
                itinerary.setAttractions(itineraryBuilder.getAttractions());

                Gson gson = new Gson();

                ItineraryCache cache = (ItineraryCache) req.getServletContext()
                        .getAttribute(Constants.ITINERARY_CACHE);

                cache.addNewItinerary(itinerary, user);
                out.println(gson.toJson(itinerary.getItineraryId()));
                resp.setStatus(200);
            });
        } catch (Exception ignore) {
        }
    }
}

