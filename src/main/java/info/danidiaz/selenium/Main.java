package info.danidiaz.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Main 
{
    private enum Strategy {
        GETTEXT
        {
            @Override
            List<List<String>> extract(RemoteWebDriver driver,JS js, WebElement element) {
                LinkedList<List<String>> result = new LinkedList<>();
                final String[] lines = 
                    element.getText().trim().split("\\r?\\n");
                for (String line:lines) {
                    result.addLast(Arrays.asList(line.trim().split("\\h+")));
                }
                return result;
            }
        },
        INJECTJS
        {
            @Override
            List<List<String>> extract(RemoteWebDriver driver,JS js,WebElement element) {
                return js.extractCells(driver, element);
            }
        },
        CELLBYCELL
        {
            @Override
            List<List<String>> extract(RemoteWebDriver driver,JS js,WebElement element) {
                return element.findElements(By.tagName("tr")).stream()
                      .map(tr -> tr.findElements(By.tagName("td")).stream()
                                   .filter(WebElement::isDisplayed)
                                   .map(WebElement::getText)
                                   .collect(Collectors.toList()))
                      .collect(Collectors.toList());
            }
        };

        abstract List<List<String>> extract(RemoteWebDriver driver,JS js,WebElement element);
    }

    private static class JS {
        private final String cellsScript;
        
        public JS(String cellsScript) {
            super();
            this.cellsScript = cellsScript;
        }

        @SuppressWarnings("unchecked")
        public List<List<String>> extractCells(RemoteWebDriver driver, WebElement element) {
            return (List<List<String>>)driver.executeScript(cellsScript, element);            
        }
    }

    public static void main( String[] args ) throws Exception
    {
        final String url = args[0];

        final JS js = buildJS();
        final RemoteWebDriver driver = new ChromeDriver(); 

        driver.get(url);

        final WebElement tableBody = 
                wait(driver).until(visibilityOfElementLocated(By.id("bigtablebody")));
        
        StopWatch watch = new StopWatch();
        for (Strategy strategy: Strategy.values()) {
            watch.reset();
            watch.start();

            List<List<String>> contents = strategy.extract(driver,js,tableBody);
            
            /*          
            if (strategy.equals(Strategy.INJECTJS)) {
                for (List<String> xs : contents) {
                    for (String x: xs) {
                        System.out.println(x);
                }
            }*/
            System.out.println(String.format("rows %d cols %d", 
                                             contents.size(),
                                             contents.get(0).size()));
            
            watch.stop();
            System.out.println(watch);
        }
        driver.quit();
    }

    private static WebDriverWait wait(WebDriver driver) {
        return new WebDriverWait(driver,10);
    }
    
    private static JS buildJS() throws IOException {
        try (InputStream is = Main.class.getResourceAsStream("extractCells.js")) {
            String cellScript = IOUtils.toString(is, "UTF-8");
            return new JS(cellScript);
        }
    }
}
