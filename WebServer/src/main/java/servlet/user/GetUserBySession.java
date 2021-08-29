package servlet.user;

import com.google.gson.Gson;
import constant.Constants;
import model.user.User;
import user.UserContext;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/getUser")
public class GetUserBySession extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletContext context = req.getServletContext();
        UserContext userContext = (UserContext) context.getAttribute(Constants.USERS_CONTEXT);
        resp.setStatus(500);

        User loggedInUser = userContext.getUserBySessionId(req.getSession().getId());

        try (PrintWriter out = resp.getWriter()) {
            UserTypeTemplate userTypeTemplate = new UserTypeTemplate(loggedInUser);
            resp.setStatus(200);
            out.println(new Gson().toJson(userTypeTemplate));
        }
    }
}
