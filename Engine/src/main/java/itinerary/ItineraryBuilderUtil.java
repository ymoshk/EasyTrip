package itinerary;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import template.Attraction;
import template.TripTag;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ItineraryBuilderUtil {
    private final QuestionsData questionsData;
    private final HashMap<String, List<template.Attraction>> attractions;

    public ItineraryBuilderUtil(HashMap<String, String> questionnaireData) {
        this.questionsData = parseQuestionsData(questionnaireData);
        this.attractions = generateAttractionsDictionary();
    }

    public QuestionsData getQuestionsData() {
        return questionsData;
    }

    private QuestionsData parseQuestionsData(HashMap<String, String> questionnaireData) {
        Gson gson = new Gson();

        String country = questionnaireData.get("country");
        String city = questionnaireData.get("city");
        int adultsCount = Integer.parseInt(questionnaireData.get("adultsCount"));
        int childrenCount = Integer.parseInt(questionnaireData.get("childrenCount"));

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime startDate = LocalDateTime.parse(questionnaireData.get("startDate"), formatter);
        LocalDateTime endDate = LocalDateTime.parse(questionnaireData.get("endDate"), formatter);

        List<TripTag> transportation = (List<TripTag>) gson.fromJson(questionnaireData.get("transportation"), List.class)
                .stream().map(data -> new TripTag((LinkedTreeMap<String, Object>) data)).collect(Collectors.toList());

        List<TripTag> favoriteAttraction = (List<TripTag>) gson.fromJson(questionnaireData.get("favoriteAttraction"), List.class)
                .stream().map(data -> new TripTag((LinkedTreeMap<String, Object>) data)).collect(Collectors.toList());

        List<TripTag> tripVibes = (List<TripTag>) gson.fromJson(questionnaireData.get("tripVibes"), List.class)
                .stream().map(data -> new TripTag((LinkedTreeMap<String, Object>) data)).collect(Collectors.toList());

        return new QuestionsData(country, city, adultsCount, childrenCount, 0,
                startDate, endDate, favoriteAttraction, tripVibes, transportation);
    }

    private HashMap<String, List<template.Attraction>> generateAttractionsDictionary() {
        HashMap<String, List<template.Attraction>> result = null;

        if (this.questionsData != null) {
            HashMap<String, List<template.Attraction>> hashMap = new HashMap<>();

            List<model.attraction.Attraction> attractionList = this.questionsData.getCity().getAttractionList();
            List<template.Attraction> attractionsTemplatesList = attractionList.stream()
                    .map(attraction -> new template.Attraction(attraction, true))
                    .collect(Collectors.toList());

            for (template.Attraction attraction : attractionsTemplatesList) {
                if (!hashMap.containsKey(attraction.type)) {
                    hashMap.put(attraction.type, new ArrayList<>());
                }

                hashMap.get(attraction.type).add(attraction);
            }

            result = hashMap;
        }

        return result;
    }

    public Itinerary getItinerary() {
        return new Itinerary(this.attractions, this.questionsData);
    }

    public HashMap<String, List<Attraction>> getAttractions() {
        return attractions;
    }
}
