package itinerary;

import algorithm.HillClimbing;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import connection.DataEngine;
import container.PriceRange;
import evaluators.AttractionEvaluator;
import template.Attraction;
import template.TripTag;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ItineraryBuilderUtil {
    private final QuestionsData questionsData;
    private final HashMap<String, List<template.Attraction>> attractions;
    private HillClimbing hillClimbing;

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
        template.Flight flight = null;  // flight is optional parameter

        List<TripTag> transportation = (List<TripTag>) gson.fromJson(questionnaireData.get("transportation"), List.class)
                .stream().map(data -> new TripTag((LinkedTreeMap<String, Object>) data)).collect(Collectors.toList());

        List<TripTag> favoriteAttraction = (List<TripTag>) gson.fromJson(questionnaireData.get("favoriteAttraction"), List.class)
                .stream().map(data -> new TripTag((LinkedTreeMap<String, Object>) data)).collect(Collectors.toList());

        List<TripTag> tripVibes = (List<TripTag>) gson.fromJson(questionnaireData.get("tripVibes"), List.class)
                .stream().map(data -> new TripTag((LinkedTreeMap<String, Object>) data)).collect(Collectors.toList());

        if(!questionnaireData.get("flight").equals("{}")){
            flight = gson.fromJson(questionnaireData.get("flight"), template.Flight.class);
        }

        return new QuestionsData(country, city, adultsCount, childrenCount, 0,
                startDate, endDate, favoriteAttraction, tripVibes, transportation, flight);
    }

    private HashMap<String, List<template.Attraction>> generateAttractionsDictionary() {
        HashMap<String, List<template.Attraction>> result = null;

        if (this.questionsData != null) {
            HashMap<String, List<template.Attraction>> hashMap = new HashMap<>();
            DataEngine dataEngine = DataEngine.getInstance();
            List<model.attraction.Attraction> attractionList = dataEngine.
                    getAttractions(questionsData.getCity().getCityName(),
                    new PriceRange(2), false);
            // save hill climbing as class member in order to reuse
            this.hillClimbing = new HillClimbing(questionsData, attractionList);
            AttractionEvaluator attractionEvaluator = this.hillClimbing.getAttractionEvaluator();
            List<String> attractionTags = this.hillClimbing.getAttractionTags();
            List<String> vibeTags = this.hillClimbing.getVibeTags();
            List<template.Attraction> attractionsTemplatesList = new ArrayList<>();
            attractionList.forEach(attraction ->{
                Attraction attractionToAdd = new template.Attraction(attraction,false);
                if(attraction.getClass().getSimpleName().equalsIgnoreCase("Restaurant")){
                    attractionToAdd.recommendedScore = attractionEvaluator.getRecommendationScore(attraction, vibeTags);
                    attractionToAdd.isRecommended = attractionEvaluator.isRecommended(attraction, vibeTags);
                }else {
                    attractionToAdd.recommendedScore = attractionEvaluator.evaluateAttraction(attraction);
                }
                attractionsTemplatesList.add(attractionToAdd);
            });


            for (template.Attraction attraction : attractionsTemplatesList) {
                if (!hashMap.containsKey(attraction.type)) {
                    hashMap.put(attraction.type, new ArrayList<>());
                }

                hashMap.get(attraction.type).add(attraction);
            }

            AtomicBoolean isPreferred = new AtomicBoolean(false);
            hashMap.keySet().forEach(attractionType->{
                List<Attraction> attractions = hashMap.get(attractionType);
                attractions = attractions.stream().sorted(Comparator.comparingDouble(value ->
                        value.recommendedScore)).collect(Collectors.toList());
                Collections.reverse(attractions);
                if(!attractionType.equalsIgnoreCase("Restaurant")){
                    isPreferred.set(attractionTags.contains(attractionType.toUpperCase()));
                    if(attractionType.equalsIgnoreCase("Museum")){
                        isPreferred.set(attractionTags.contains("ART"));
                    }
                    attractionEvaluator.setRecommendedAttractions(attractions, isPreferred.get());
                }
                hashMap.put(attractionType, attractions);
            });

            result = hashMap;
        }

        return result;
    }

    public Itinerary getItinerary() {
        return new Itinerary(this.attractions, this.questionsData);
    }

    public HashMap<String, List<Attraction>>getAttractions () {
        return attractions;
    }

    public HillClimbing getHillClimbing() {
        return hillClimbing;
    }
}
