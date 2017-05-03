package info.danidiaz.selenium;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Hello world!
 *
 */
public class Main 
{
    public static void main( String[] args )
    {
        final String url = args[0];
        System.out.println(url);
        final RemoteWebDriver driver = new ChromeDriver(); 
        driver.quit();
    }
}
