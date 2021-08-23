package servlet.itinerary;

import com.google.gson.Gson;
import connection.DataEngine;
import itinerary.Itinerary;
import log.LogsManager;
import model.flightOffer.FlightOffer;
import model.itinerary.ItineraryModel;
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
            out.println(new Gson().toJson(itineraries));
        } catch (Exception e) {
            LogsManager.logException(e);
            resp.setStatus(500);
        }

    }
}
