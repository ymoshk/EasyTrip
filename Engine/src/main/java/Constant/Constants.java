package Constant;

import model.attraction.Attraction;
import model.attraction.amusement.park.AmusementPark;
import model.attraction.aquarium.Aquarium;
import model.attraction.art.gallery.ArtGallery;
import model.attraction.cafe.Cafe;
import model.attraction.camp.ground.CampGround;
import model.attraction.casino.Casino;
import model.attraction.hotel.Hotel;
import model.attraction.museum.Museum;
import model.attraction.other.OtherAttraction;
import model.attraction.park.Park;
import model.attraction.restaurant.Restaurant;
import model.attraction.shopping.mall.ShoppingMall;
import model.attraction.spa.Spa;
import model.attraction.zoo.Zoo;
import model.bar.Bar;
import model.nightClub.NightClub;

import java.util.Base64;
import java.util.HashMap;

public class Constants {

    private static final HashMap<String, Integer> AttractionsEST = new HashMap<>();

    static {
        AttractionsEST.put(Cafe.class.getSimpleName(), 60);
        AttractionsEST.put(Bar.class.getSimpleName(), 120);
        AttractionsEST.put(NightClub.class.getSimpleName(), 180);
        AttractionsEST.put(Hotel.class.getSimpleName(), 30); //TODO - handle the time
        AttractionsEST.put(Aquarium.class.getSimpleName(), 180);
        AttractionsEST.put(ArtGallery.class.getSimpleName(), 120);
        AttractionsEST.put(AmusementPark.class.getSimpleName(), 120);
        AttractionsEST.put(CampGround.class.getSimpleName(), 120);
        AttractionsEST.put(Casino.class.getSimpleName(), 120);
        AttractionsEST.put(Museum.class.getSimpleName(), 180);
        AttractionsEST.put(OtherAttraction.class.getSimpleName(), 60);
        AttractionsEST.put(Park.class.getSimpleName(), 90);
        AttractionsEST.put(Restaurant.class.getSimpleName(), 90);
        AttractionsEST.put(Spa.class.getSimpleName(), 60);
        AttractionsEST.put(ShoppingMall.class.getSimpleName(), 120);
        AttractionsEST.put(Zoo.class.getSimpleName(), 150);
    }

    public static int getESTOfAttraction(String attractionType){
        return AttractionsEST.get(attractionType);
    }

    public static int getESTOfAttraction(Class attractionClass){
        return AttractionsEST.get(attractionClass.getSimpleName());
    }

    public static int getESTOfAttraction(Attraction attraction){
        return AttractionsEST.get(attraction.getClass().getSimpleName());
    }
}
