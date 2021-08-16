package model.attraction;

import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import container.PriceRange;
import model.attraction.amusement.park.AmusementPark;
import model.attraction.aquarium.Aquarium;
import model.attraction.art.gallery.ArtGallery;
import model.attraction.beach.Beach;
import model.attraction.cafe.Cafe;
import model.attraction.casino.Casino;
import model.attraction.hotel.Hotel;
import model.attraction.market.Market;
import model.attraction.museum.Museum;
import model.attraction.topSight.TopSight;
import model.attraction.touristAttraction.TouristAttraction;
import model.attraction.park.Park;
import model.attraction.restaurant.Restaurant;
import model.attraction.shopping.mall.ShoppingMall;
import model.attraction.spa.Spa;
import model.attraction.zoo.Zoo;
import model.attraction.bar.Bar;
import model.location.City;
import model.attraction.nightClub.NightClub;

import java.util.Arrays;

public class AttractionsFactory {

    public static Attraction getAttraction(PlacesSearchResult searchResultObject, PlaceType placeType, PriceRange priceRange, City city) {
        Attraction res = null;

        switch (placeType) {
            case LODGING:
                res = new Hotel(searchResultObject, placeType, priceRange, city);
                break;

            case CAFE:
                res = new Cafe(searchResultObject, placeType, priceRange, city);
                break;

            case RESTAURANT:
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
                res = new Bar(searchResultObject, placeType, priceRange, city);
                break;

            case NIGHT_CLUB:
                res = new NightClub(searchResultObject, placeType, priceRange, city);
                break;

            case CASINO:
                res = new Casino(searchResultObject, placeType, priceRange, city);
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
                res = new TouristAttraction(searchResultObject, placeType, priceRange, city);
                break;

            case SPA:
                res = new Spa(searchResultObject, placeType, priceRange, city);
                break;

            case GROCERY_OR_SUPERMARKET:
                res = new Market(searchResultObject, placeType, priceRange, city);
                break;

            // DOCTOR == BEACH (beach doesn't exist on google type & time is short)
            case DOCTOR:
                res = new Beach(searchResultObject, placeType, priceRange, city);
                break;

            // ATM == TOP SIGHT
            case ATM:
                res = new TopSight(searchResultObject, placeType, priceRange, city);
                break;
        }

        return res;
    }

    public static void setAttractionDetails(Attraction attraction, PlaceDetails placeDetails){
        if(placeDetails == null || attraction == null){
            return;
        }

        if(placeDetails.openingHours != null)
            attraction.setOpeningHours(placeDetails.openingHours);
        if(placeDetails.priceLevel != null)
            attraction.setPriceLevel(placeDetails.priceLevel);
        if(placeDetails.formattedPhoneNumber != null)
            attraction.setLocalNumber(placeDetails.formattedPhoneNumber);
        if(placeDetails.internationalPhoneNumber != null)
            attraction.setInternationalNumber(placeDetails.internationalPhoneNumber);
        if(placeDetails.website != null)
            attraction.setWebsite(placeDetails.website.toString());
        if(placeDetails.reviews != null){
            attraction.setReviews(Arrays.asList(placeDetails.reviews));
        }
    }
}
