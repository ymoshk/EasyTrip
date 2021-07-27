package servlet.schedule;

import util.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

@WebServlet("/api/completeQuestions")
public class QuestionsCompleted extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HashMap<String, String> reqArgs = Utils.parsePostData(req);

        reqArgs.forEach((key, val) -> System.out.println("key: " + key + " val: " + val));

        String country = reqArgs.get("country");
        String city = reqArgs.get("city");
        int adultsCount = Integer.parseInt(reqArgs.get("adultsCount"));
        int childrenCount = Integer.parseInt(reqArgs.get("childrenCount"));
        int budget = Integer.parseInt(reqArgs.get("budget"));
        LocalDateTime startDate = LocalDateTime.parse(reqArgs.get("startDate"));
        LocalDateTime endDate = LocalDateTime.parse(reqArgs.get("endDate"));
        //        HashMap<String, Boolean>

    }
}
