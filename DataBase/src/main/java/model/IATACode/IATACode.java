package model.IATACode;

import model.Model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class IATACode extends Model {
    private String city;
    private String country;
    private String code;

    public IATACode(String city, String country, String code) {
        this.city = city;
        this.country = country;
        this.code = code;
    }

    public IATACode() {

    }

    public String getCode() {
        return code.trim();
    }

    @Override
    public String toString() {
        return "IATACode{" +
                "city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
