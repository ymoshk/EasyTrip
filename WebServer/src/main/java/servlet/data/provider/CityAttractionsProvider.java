package servlet.data.provider;

import com.google.gson.Gson;
import connection.DataEngine;
import constant.Constants;
import model.attraction.Attraction;
import model.location.City;
import util.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/getCityAttractions")
public class CityAttractionsProvider extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HashMap<String, String> reqData = Utils.parsePostData(req);

        DataEngine dataEngine = (DataEngine) req.getServletContext().getAttribute(Constants.DATA_ENGINE);
        City city = dataEngine.getCity(reqData.get("cityName")).orElse(null);
        Gson gson = new Gson();

        try (PrintWriter out = resp.getWriter()) {
            if (city != null) {
                // TODO this method returns any attraction marked as recommended - change it
                HashMap<String, List<template.Attraction>> hashMap = new HashMap<>();
                List<Attraction> attractionList = city.getAttractionList();
                List<template.Attraction> attractionsTemplatesList = attractionList.stream()
                        .map(attraction -> new template.Attraction(attraction, true, false))
                        .collect(Collectors.toList());

                for (template.Attraction attraction : attractionsTemplatesList) {
                    if (!hashMap.containsKey(attraction.type)) {
                        hashMap.put(attraction.type, new ArrayList<>());
                    }

                    hashMap.get(attraction.type).add(attraction);
                }

                out.println(gson.toJson(hashMap));
            } else {
                HashMap<String, List<template.Attraction>> result = new HashMap<>();
                out.println(gson.toJson(result));
            }
        }
    }
}
