package info.danidiaz.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Hello world!
 *
 */
public class Main 
{
    public static void main( String[] args )
    {
        final String url = args[0];
        final By byTableBody = By.id("bigtablebody");

        final RemoteWebDriver driver = new ChromeDriver(); 
        driver.get(url);
        wait(driver).until(visibilityOfElementLocated(byTableBody));
        driver.quit();
    }
    
    private static WebDriverWait wait(WebDriver driver) {
        return new WebDriverWait(driver,10);
    }
}
