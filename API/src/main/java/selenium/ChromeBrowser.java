package selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

public class ChromeBrowser implements Closeable {
    private final WebDriver webDriver;
    private final WebDriverWait waitUnit;

    public ChromeBrowser() {
        System.setProperty("webdriver.chrome.driver", "API/src/main/resources/webDriver/chromedriver.exe");
        this.webDriver = new ChromeDriver(buildChromeOptions());
        this.waitUnit = new WebDriverWait(this.webDriver, 20);
    }

    private ChromeOptions buildChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        // Set regular chrome browser user agent
        options.addArguments("userAgent\": 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.53 Safari/537.36");
        //        options.addArguments("window-size=1280,800");

        options.addArguments("start-maximized");
        options.addArguments("--disable-extensions");
        options.setExperimentalOption("excludeSwitches", "enable-automation");
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        // Make browser invisible
        //        options.addArguments("--headless");
        options.addArguments("incognito");

        return options;
    }

    public synchronized List<WebElement> getElementsList(String url, By method, By waitToBeClickable) {
        List<WebElement> result = new ArrayList<>();
        this.webDriver.get(url);
        this.waitUnit.until(ExpectedConditions.elementToBeClickable(waitToBeClickable));

        try {
            result = webDriver.findElements(method);
        } catch (Exception ignored) {

        }
        return result;
    }

    public synchronized WebElement getElement(String url, By method, By waitToBeClickable) {
        WebElement result;
        this.webDriver.get(url);
        this.waitUnit.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(waitToBeClickable));

        try {
            result = webDriver.findElement(method);
        } catch (Exception ignored) {
            result = null;
        }
        return result;
    }

    @Override
    public void close() {
        this.webDriver.close();
    }
}


