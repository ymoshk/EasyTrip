package selenium.sky.scanner.search;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import selenium.sky.scanner.SkyScannerSearch;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NoDestSearch extends SkyScannerSearch {

    public NoDestSearch(String sourceCity, LocalDateTime flightDate) {
        this(sourceCity, flightDate, null, null, null);
    }

    public NoDestSearch(String sourceCity, LocalDateTime flightDate, LocalDateTime returnDate) {
        this(sourceCity, flightDate, returnDate, null, null);
    }

    public NoDestSearch(String sourceCity, LocalDateTime flightDate, LocalDateTime returnDate, Integer adultsCount) {
        this(sourceCity, flightDate, returnDate, adultsCount, null);
    }

    public NoDestSearch(String sourceCity, LocalDateTime flightDate, LocalDateTime returnDate,
                        Integer adultsCount, Integer childCount) throws InvalidParameterException {
        if (sourceCity == null || flightDate == null) {
            throw new InvalidParameterException("Flight search uri parts aren't valid");
        } else {

            addUriPart(sourceCity);
            addUriPart(convertDateToFormat(flightDate));

            if (returnDate != null) {
                addUriPart(convertDateToFormat(returnDate));
            }

            if (adultsCount != null) {
                addParam("adults", adultsCount.toString());
            }

            if (childCount != null) {
                addParam("children", childCount.toString());
            }
        }
    }

    public List<String> getPossibleDestinations() {
        By method = new By.ByXPath("//*[@id=\"destinations\"]/ul/li");
        By wait = new By.ByXPath("//*[@id=\"destinations\"]/ul/li[*]/a/div/h3");
        return ParseCountries(this.browser.getElementsList(getUrl(), method, wait));
    }

    private List<String> ParseCountries(List<WebElement> elementsList) {
        List<String> result = new ArrayList<>(); // TODO - change to countries list

        for (WebElement element : elementsList) {
            result.add(element.getAttribute("InnerText"));
        }
        return result;
    }
}
