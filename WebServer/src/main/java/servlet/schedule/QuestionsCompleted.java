package servlet.schedule;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import schedule.QuestionsData;
import template.TripTag;
import util.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/completeQuestions")
public class QuestionsCompleted extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HashMap<String, String> reqArgs = Utils.parsePostData(req);
        Gson gson = new Gson();

        String country = reqArgs.get("country");
        String city = reqArgs.get("city");
        int adultsCount = Integer.parseInt(reqArgs.get("adultsCount"));
        int childrenCount = Integer.parseInt(reqArgs.get("childrenCount"));
        int budget = Integer.parseInt(reqArgs.get("budget"));

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime startDate = LocalDateTime.parse(reqArgs.get("startDate"), formatter);
        LocalDateTime endDate = LocalDateTime.parse(reqArgs.get("endDate"), formatter);

        List<TripTag> favoriteAttraction = (List<TripTag>) gson.fromJson(reqArgs.get("favoriteAttraction"), List.class)
                .stream().map(data -> new TripTag((LinkedTreeMap<String, Object>) data)).collect(Collectors.toList());

        List<TripTag> tripVibes = (List<TripTag>) gson.fromJson(reqArgs.get("tripVibes"), List.class)
                .stream().map(data -> new TripTag((LinkedTreeMap<String, Object>) data)).collect(Collectors.toList());

        try {
            QuestionsData data = new QuestionsData(country, city, adultsCount, childrenCount, budget,
                    startDate, endDate, favoriteAttraction, tripVibes);

            //TODO create a schedule object that contains the data and return the client it's unique id.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
