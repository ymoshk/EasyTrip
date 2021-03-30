package selenium.sky.scanner;

import org.openqa.selenium.WebElement;
import selenium.sky.scanner.search.NoDestSearch;

import java.time.LocalDateTime;
import java.util.List;

public class Test {

    public static void test() {

        NoDestSearch search = new NoDestSearch("tlv", LocalDateTime.now());
        List<String> elementList = search.getPossibleDestinations();
        elementList.forEach(System.out::println);
//        search.close();
    }
}
