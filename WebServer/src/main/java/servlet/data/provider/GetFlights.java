package servlet.data.provider;

import com.google.gson.Gson;
import connection.FlightEngine;
import constant.Constants;
import model.flightOffer.FlightOffer;
import template.Flight;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/getFlights")
public class GetFlights extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        FlightEngine flightEngine = (FlightEngine) request.getServletContext().getAttribute(Constants.FLIGHT_ENGINE);

        List<FlightOffer> flightOfferList = flightEngine.findFlights("Israel",
                "Tel Aviv",
                "Germany",
                "Berlin",
                LocalDate.parse("2021-08-30"),
                LocalDate.parse("2021-09-05"),
                false,
                1);

        Gson gson = new Gson();

        try(PrintWriter out = response.getWriter()) {
            List<template.Flight> flightList = flightOfferList.stream()
                    .map(Flight::new)
                    .collect(Collectors.toList());

            out.println(gson.toJson(flightList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
