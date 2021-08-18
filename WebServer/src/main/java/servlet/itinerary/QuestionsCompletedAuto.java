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
import util.Utils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

@WebServlet("/api/completeQuestionsAuto")
public class QuestionsCompletedAuto extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ItineraryBuilderUtil itineraryBuilder = new ItineraryBuilderUtil(Utils.parsePostData(req));

        QuestionsData questionsData = itineraryBuilder.getQuestionsData();
        List<Attraction> attractionList = questionsData.getCity().getAttractionList();
        HillClimbing hillClimbing = new HillClimbing(questionsData, attractionList);
        State state = new State(new Itinerary(new HashMap<>(), questionsData), 0.0);
        Itinerary itinerary = hillClimbing.getItineraryWithHillClimbingAlgorithm(state);

        Gson gson = new Gson();

        if (itinerary != null) {
            ItineraryCache cache = (ItineraryCache) req.getServletContext()
                    .getAttribute(Constants.ITINERARY_CACHE);

            cache.addNewItinerary(itinerary);

            try (PrintWriter out = resp.getWriter()) {
                out.println(gson.toJson(itinerary.getItineraryId()));
            }
        } else {
            resp.setStatus(500);
        }
    }
}

