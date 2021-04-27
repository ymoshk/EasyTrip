package connection;

import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import model.attraction.Attraction;
import model.location.City;
import model.travel.Travel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


class DataEngineTest {

    @Test
    void test(){
        try {
            DataEngine eng = DataEngine.getInstance();
            City ramatGan = eng.getCities("Ramat").get(0);

            Attraction source = (Attraction) ramatGan.getAttractionList().stream().filter(attraction -> attraction.getName().equals("Safsal")).toArray()[0];
            Attraction dest = (Attraction) ramatGan.getAttractionList().stream().filter(attraction -> attraction.getName().equals("Shemesh")).toArray()[0];

            Travel travel = eng.getTravel(source.getGeometry().location, dest.getGeometry().location, TravelMode.WALKING);
             travel = eng.getTravel(source.getGeometry().location, dest.getGeometry().location, TravelMode.WALKING);

            Integer x = new Integer(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateAttraction()
    {
        List<Attraction> res = getAttractionInSquare(new LatLng(32.0842711, 34.8078767), 2);

        AtomicInteger i = new AtomicInteger(1);
        res.forEach(attraction -> {
            System.out.println(i + "." + attraction.getName());
            i.getAndIncrement();
        });
    }

    private List<Attraction> getAttractionInSquare(LatLng origin, double km){
        double factor = (km/1.11) * 0.01;

        LatLng topRight = new LatLng(origin.lat + factor, origin.lng + factor);
        LatLng topLeft = new LatLng(origin.lat + factor, origin.lng - factor);
        LatLng bottomLeft = new LatLng(origin.lat - factor, origin.lng - factor);

        List<Attraction> res = (List<Attraction>) DBContext.getInstance().selectQuery("FROM Attraction WHERE " +
                "lat > " + bottomLeft.lat + " AND lat < " + topLeft.lat +
                " AND lng > " + topLeft.lng + " AND lng < " + topRight.lng);

        return res;
    }

}