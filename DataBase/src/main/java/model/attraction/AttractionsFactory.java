package model.attraction;

import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import container.PriceRange;
import model.attraction.amusement.park.AmusementPark;
import model.attraction.aquarium.Aquarium;
import model.attraction.art.gallery.ArtGallery;
import model.attraction.camp.ground.CampGround;
import model.attraction.casino.Casino;
import model.attraction.hotel.Hotel;
import model.attraction.museum.Museum;
import model.attraction.night.life.NightLife;
import model.attraction.other.OtherAttraction;
import model.attraction.park.Park;
import model.attraction.restaurant.Restaurant;
import model.attraction.shopping.mall.ShoppingMall;
import model.attraction.zoo.Zoo;
import model.location.City;

import java.util.Arrays;

public class AttractionsFactory {

    public static Attraction getAttraction(PlacesSearchResult searchResultObject, PlaceType placeType, PriceRange priceRange, City city) {
        Attraction res = null;

        switch (placeType) {
            case LODGING:
                res = new Hotel(searchResultObject, placeType, priceRange, city);
                break;

            case RESTAURANT:
            case CAFE:
                res = new Restaurant(searchResultObject, placeType, priceRange, city);
                break;

            case MUSEUM:
                res = new Museum(searchResultObject, placeType, priceRange, city);
                break;

            case AMUSEMENT_PARK:
                res = new AmusementPark(searchResultObject, placeType, priceRange, city);
                break;

            case AQUARIUM:
                res = new Aquarium(searchResultObject, placeType, priceRange, city);
                break;

            case ART_GALLERY:
                res = new ArtGallery(searchResultObject, placeType, priceRange, city);
                break;

            case BAR:
            case NIGHT_CLUB:
                res = new NightLife(searchResultObject, placeType, priceRange, city);
                break;

            case CASINO:
                res = new Casino(searchResultObject, placeType, priceRange, city);
                break;

            case CAMPGROUND:
                res = new CampGround(searchResultObject, placeType, priceRange, city);
                break;

            case PARK:
                res = new Park(searchResultObject, placeType, priceRange, city);
                break;

            case SHOPPING_MALL:
                res = new ShoppingMall(searchResultObject, placeType, priceRange, city);
                break;

            case ZOO:
                res = new Zoo(searchResultObject, placeType, priceRange, city);
                break;

            case TOURIST_ATTRACTION:
                res = new OtherAttraction(searchResultObject, placeType, priceRange, city);
                break;
        }

        return res;
    }

    public static void setAttractionDetails(Attraction attraction, PlaceDetails placeDetails){
        attraction.setOpeningHours(placeDetails.openingHours);
        attraction.setPriceLevel(placeDetails.priceLevel);
        attraction.setLocalNumber(placeDetails.formattedPhoneNumber);
        attraction.setInternationalNumber(placeDetails.internationalPhoneNumber);
        if(placeDetails.website != null)
            attraction.setWebsite(placeDetails.website.toString());
        attraction.setReviews(Arrays.asList(placeDetails.reviews));
    }
}
