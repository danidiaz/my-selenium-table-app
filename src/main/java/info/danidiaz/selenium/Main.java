package info.danidiaz.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Main 
{
    private enum Strategy {
        GETTEXT,
        CELLBYCELL,
        INJECTJS
    }

    public static void main( String[] args )
    {
        final String url = args[0];
        final By tableBodyLoc = By.id("bigtablebody");

        final RemoteWebDriver driver = new ChromeDriver(); 
        driver.get(url);
        final WebElement tableBody = 
                wait(driver).until(visibilityOfElementLocated(tableBodyLoc));
        
        StopWatch watch = new StopWatch();
        for (Strategy strategy: Strategy.values()) {
            watch.reset();
            watch.start();

            List<List<String>> contents = extract(strategy,driver,tableBody);
            
            System.out.println(String.format("rows %d cols %d", 
                                             contents.size(),
                                             contents.get(0).size()));
            
            watch.stop();
            System.out.println(watch);
        }
        driver.quit();
    }
    
    private static List<List<String>> extract(Strategy strategy,WebDriver driver,WebElement element) {
        switch (strategy) {
            case GETTEXT:
                return extractGETTEXT(element);
            case CELLBYCELL:
                return extractCELLBYCELL(element);
            case INJECTJS:
                return extractINJECTJS(element);
        }
        return null;
    }
    
    private static List<List<String>> extractGETTEXT(WebElement element) {
        LinkedList<List<String>> result = new LinkedList<>();
        final String[] lines = 
            element.getText().trim().split("\\r?\\n");
        for (String line:lines) {
            result.addLast(Arrays.asList(line.trim().split("\\h+")));
        }
        return result;
    }
    
    private static List<List<String>> extractCELLBYCELL(WebElement element) {
        return element.findElements(By.tagName("tr")).stream()
                      .map(tr -> tr.findElements(By.tagName("td")).stream()
                                   .filter(WebElement::isDisplayed)
                                   .map(WebElement::getText)
                                   .collect(Collectors.toList()))
                      .collect(Collectors.toList());
    }

    private static List<List<String>> extractINJECTJS(WebElement element) {
        return null;
    }

    private static WebDriverWait wait(WebDriver driver) {
        return new WebDriverWait(driver,10);
    }
}
