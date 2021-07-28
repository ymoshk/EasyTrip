package servlet.data.provider;

import com.google.gson.Gson;
import connection.DataEngine;
import constant.Constants;
import model.location.Country;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/getCountryNames")
public class CountryNamesProvider extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DataEngine dataEngine = (DataEngine) req.getServletContext()
                .getAttribute(Constants.DATA_ENGINE);

        List<String> names = dataEngine.getCountries()
                .stream()
                .map(Country::getCountryName)
                .collect(Collectors.toList());

        Gson gson = new Gson();

        try (PrintWriter out = resp.getWriter()) {
            out.println(gson.toJson(names));
        }
    }
}
