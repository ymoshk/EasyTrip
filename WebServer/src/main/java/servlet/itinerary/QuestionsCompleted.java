package servlet.itinerary;

import cache.ItineraryCache;
import com.google.gson.Gson;
import constant.Constants;
import itinerary.Itinerary;
import itinerary.ItineraryBuilderUtil;
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
        ItineraryBuilderUtil itineraryBuilder = new ItineraryBuilderUtil(Utils.parsePostData(req));
        Itinerary itinerary = itineraryBuilder.getItinerary();
        Gson gson = new Gson();

        if (itinerary != null) {
            ItineraryCache cache = (ItineraryCache) req.getServletContext()
                    .getAttribute(Constants.ITINERARY_CACHE);

            Thread savingThread = new Thread(() -> cache.addNewItinerary(itinerary));
            savingThread.start();

            try (PrintWriter out = resp.getWriter()) {
                out.println(gson.toJson(itinerary.getItineraryId()));
            }
        } else {
            resp.setStatus(500);
        }
    }
}

