package model;

import com.google.maps.model.LatLng;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.List;

@Entity
public class Country extends Model implements Serializable {

    @ElementCollection
    List<String> timezones;
    LatLng countryLatLong;
    String countryName;
    String localeCode;
    String capital;
    double averagePricePerDay;
    // TODO
//    List<City> cityList = new ArrayList<>();

    public Country() {

    }

    public Country(List<String> timezones, LatLng countryLatLong, String countryName, String localeCode,
                   String capital) {
        this.timezones = timezones;
        this.countryLatLong = countryLatLong;
        this.countryName = countryName;
        this.localeCode = localeCode;
        this.capital = capital;
    }


    public List<String> getTimezones() {
        return timezones;
    }

    public void setTimezones(List<String> timezones) {
        this.timezones = timezones;
    }

    public LatLng getCountryLatLong() {
        return countryLatLong;
    }

    public void setCountryLatLong(LatLng countryLatLong) {
        this.countryLatLong = countryLatLong;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public double getAveragePricePerDay() {
        return averagePricePerDay;
    }

    public void setAveragePricePerDay(double averagePricePerDay) {
        this.averagePricePerDay = averagePricePerDay;
    }
//
//    public List<City> getCityList() {
//        return cityList;
//    }
//
//    public void setCityList(List<City> cityList) {
//        this.cityList = cityList;
//    }
}
