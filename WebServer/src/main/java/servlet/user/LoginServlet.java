package servlet.user;

import com.google.gson.Gson;
import constant.Constants;
import user.UserContext;
import util.Utils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        UserContext userContext = (UserContext) context.getAttribute(Constants.USERS_CONTEXT);
        HashMap<String, String> data = Utils.parsePostData(req);
        resp.setStatus(500);

        String userName = data.get("userName");
        String password = data.get("password");
        String sessionId = req.getSession(false).getId();

        if (userContext.login(sessionId, userName, password)) {
            try (PrintWriter out = resp.getWriter()) {
                out.println(new Gson().toJson(userContext.getLoggedInUser(sessionId)));
                resp.setStatus(200);
            }
        }
    }
}
