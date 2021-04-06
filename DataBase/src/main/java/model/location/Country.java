package model.location;

import com.google.maps.model.LatLng;
import model.Model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Cacheable
public class Country extends Model implements Serializable {
    @ElementCollection
    private List<String> timezones = new ArrayList<>();
    private LatLng countryLatLong;
    @Column(nullable = false)
    private String countryName;
    @Column(unique = true, nullable = false)
    private String localeCode;
    @Column(nullable = false)
    private String capital;
    private double averagePricePerDay;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "country_id", insertable = true, updatable = true)
    private List<City> cityList;

    public Country() {
        setCreateTime(LocalDateTime.now());
        setUpdateTime(LocalDateTime.now());
    }

    public Country(List<String> timezones, LatLng countryLatLong, String countryName, String localeCode,
                   String capital) {
        this.timezones = timezones;
        this.countryLatLong = countryLatLong;
        this.countryName = countryName;
        this.localeCode = localeCode;
        this.capital = capital;
        setCreateTime(LocalDateTime.now());
        setUpdateTime(LocalDateTime.now());
    }

    public void addCityToCityList(City cityToAdd) {
        if (cityList == null)
            cityList = new ArrayList<>();
        cityList.add(cityToAdd);
    }

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
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
