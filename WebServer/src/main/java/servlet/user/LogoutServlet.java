package servlet.user;

import com.google.gson.Gson;
import constant.Constants;
import user.UserContext;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletContext context = req.getServletContext();
        UserContext userContext = (UserContext) context.getAttribute(Constants.USERS_CONTEXT);
        resp.setStatus(500);

        req.getSession().invalidate();
        String sessionId = req.getSession(false).getId();
        userContext.logout(sessionId);

        try (PrintWriter out = resp.getWriter()) {
            out.println(new Gson().toJson(userContext.getLoggedInUser(sessionId)));
            resp.setStatus(200);
        }
    }
}
