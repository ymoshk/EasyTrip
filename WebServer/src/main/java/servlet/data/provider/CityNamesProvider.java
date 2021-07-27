package servlet.data.provider;

import com.google.gson.Gson;
import connection.DataEngine;
import constant.Constants;
import model.location.City;
import model.location.Country;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/getCities")
public class CityNamesProvider extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DataEngine dataEngine = (DataEngine) req.getServletContext()
                .getAttribute(Constants.DATA_ENGINE);
        try (PrintWriter out = resp.getWriter()) {
            Gson gson = new Gson();
            List<String> cities = new ArrayList<>();

            if (!req.getParameter("country").isEmpty()) {
                List<Country> countryList = dataEngine.getCountries(req.getParameter("country"));

                if (countryList.size() == 1) {
                    cities = countryList.get(0).getCityList()
                            .stream()
                            .map(City::getCityName)
                            .collect(Collectors.toList());
                }
            }
            out.println(gson.toJson(cities));
        }
    }
}

