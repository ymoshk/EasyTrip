package servlet.data.provider;

import com.google.gson.Gson;
import connection.FlightEngine;
import constant.Constants;
import model.flightOffer.FlightOffer;
import template.Flight;
import util.Utils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/getFlights")
public class GetFlights extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        FlightEngine flightEngine = (FlightEngine) request.getServletContext().getAttribute(Constants.FLIGHT_ENGINE);
        HashMap<String, String> requestData = Utils.parsePostData(request);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        List<FlightOffer> flightOfferList = flightEngine.findFlights(requestData.get("originCountry"),
                requestData.get("originCity"),
                requestData.get("destinationCountry"),
                requestData.get("destinationCity"),
                LocalDateTime.parse(requestData.get("departureDate"), formatter).toLocalDate(),
                LocalDateTime.parse(requestData.get("returnDate"), formatter).toLocalDate(),
                Boolean.parseBoolean(requestData.get("oneWay")),
                Integer.parseInt(requestData.get("numberOfPassengers")));

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
