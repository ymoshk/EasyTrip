package servlet.itinerary;

import com.google.gson.Gson;
import connection.DataEngine;
import constant.Constants;
import itinerary.Itinerary;
import itinerary.QuestionsData;
import log.LogsManager;
import model.itinerary.ItineraryModel;
import model.itinerary.ItineraryStatus;
import model.user.User;
import user.UserContext;
import util.Utils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = "/api/getUserItineraries")
public class GetUserItineraries extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        UserContext userContext = (UserContext) Utils.getContext(req).getAttribute(Constants.USERS_CONTEXT);
        resp.setStatus(500);
        Optional<User> loggedInUser = userContext.getLoggedInUser(req.getSession(false).getId());
//TODO - Replace line 30 with the note
        loggedInUser.ifPresent(user -> {
//        userContext.getLoggedInUser("48b5b6d6-f733-4abd-9a33-63e0688e9c62").ifPresent(user -> {
            String userName = user.getUserName();
            try (PrintWriter out = resp.getWriter()) {
                List<ItineraryModel> itineraries = DataEngine.getInstance().getUserItineraries(userName);

                List<ItineraryAndStatus> res =
                        itineraries.stream().
                                map(itineraryModel -> {
                                    Itinerary itinerary = new Gson().fromJson(itineraryModel.getJsonData(), Itinerary.class);
                                    return new ItineraryAndStatus(itinerary.getQuestionsData(), itineraryModel.getStatus(), itinerary.getItineraryId());
                                }).collect(Collectors.toList());

                out.println(new Gson().toJson(res));
                resp.setStatus(200);
            } catch (Exception e) {
                LogsManager.logException(e);
            }
        });
    }

    private static class ItineraryAndStatus {
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
