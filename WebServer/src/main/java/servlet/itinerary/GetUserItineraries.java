package servlet.itinerary;

import cache.ItineraryCache;
import com.google.gson.Gson;
import connection.DataEngine;
import itinerary.Itinerary;
import itinerary.QuestionsData;
import log.LogsManager;
import model.flightOffer.FlightOffer;
import model.itinerary.ItineraryModel;
import model.itinerary.ItineraryStatus;
import util.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

@WebServlet(urlPatterns = "/api/getUserItineraries")
public class GetUserItineraries extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HashMap<String, String> data = Utils.parsePostData(req);
        String userId = data.get("userID");

        try (PrintWriter out = resp.getWriter()) {
            List<ItineraryModel> itineraries = DataEngine.getInstance().getUserItineraries(userId);

            List<ItineraryAndStatus> res =
                    (List<ItineraryAndStatus>) itineraries.stream().
                            map(itineraryModel ->
                            {
                                Itinerary itinerary = new Gson().fromJson(itineraryModel.getJsonData(), Itinerary.class);
                                return new ItineraryAndStatus(itinerary.getQuestionsData(), itineraryModel.getStatus(), itinerary.getItineraryId());
                            });

            out.println(new Gson().toJson(res));
        } catch (Exception e) {
            LogsManager.logException(e);
            resp.setStatus(500);
        }
    }

    private class ItineraryAndStatus {
        QuestionsData questionsData;
        model.itinerary.ItineraryStatus itineraryStatus;
        String itineraryId;

        private ItineraryAndStatus(QuestionsData questionsData, ItineraryStatus itineraryStatus, String itineraryId) {
            this.questionsData = questionsData;
            this.itineraryStatus = itineraryStatus;
            this.itineraryId = itineraryId;
        }
    }
}
