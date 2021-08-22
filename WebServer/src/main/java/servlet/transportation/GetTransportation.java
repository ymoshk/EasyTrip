package servlet.transportation;

import com.google.gson.Gson;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import connection.DataEngine;
import itinerary.ActivityNode;
import model.travel.Travel;
import util.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/getTransportation")
public class GetTransportation extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HashMap<String, String> data = Utils.parsePostData(req);
        double srcLat = Double.parseDouble(data.get("srcLat"));
        double srcLng = Double.parseDouble(data.get("srcLng"));
        double destLat = Double.parseDouble(data.get("destLat"));
        double destLng = Double.parseDouble(data.get("destLng"));
        DataEngine eng = DataEngine.getInstance();
        Travel walking = eng.getTravel(new LatLng(srcLat, srcLng), new LatLng(destLat, destLng), TravelMode.WALKING);
        Travel driving = eng.getTravel(new LatLng(srcLat, srcLng), new LatLng(destLat, destLng), TravelMode.DRIVING);
        Travel transit = eng.getTravel(new LatLng(srcLat, srcLng), new LatLng(destLat, destLng), TravelMode.TRANSIT);

        try (PrintWriter out = resp.getWriter()) {
            Map<String, Long> res = new HashMap<>();
            res.put("driving", driving.getDistanceMatrixElement().duration.inSeconds / 60);
            res.put("walking", walking.getDistanceMatrixElement().duration.inSeconds / 60);
            res.put("transit", transit.getDistanceMatrixElement().duration.inSeconds / 60);

            out.println(new Gson().toJson(res));
        } catch (Exception e) {
            resp.setStatus(500);
        }
    }
}
