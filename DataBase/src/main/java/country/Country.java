package country;

import city.City;
import com.google.maps.model.LatLng;
import model.Model;

import java.util.List;

public class Country extends Model {
    String countryName;
    LatLng countryCenter;
    double averagePricePerDay;
    List<City> cityList;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public LatLng getCountryCenter() {
        return countryCenter;
    }

    public void setCountryCenter(LatLng countryCenter) {
        this.countryCenter = countryCenter;
    }

    public double getAveragePricePerDay() {
        return averagePricePerDay;
    }

    public void setAveragePricePerDay(double averagePricePerDay) {
        this.averagePricePerDay = averagePricePerDay;
    }

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }
}
