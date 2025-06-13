package com.mesofi.myth.collection.stats;

import java.time.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WebScraper {
  public static void main(String[] args) {
    ChromeOptions options = new ChromeOptions();
    // options.addArguments("--headless");
    options.addArguments("--no-sandbox");
    options.addArguments("--disable-dev-shm-usage");
    options.addArguments(
        "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

    WebDriver driver = new ChromeDriver(options);

    try {
      driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(40));
      driver.get("https://www.yoyakunow.com/en/search?controller=search&s=myth+cloth&page=1");

      // Wait for page to load
      Thread.sleep(40000);

      String pageSource = driver.getPageSource();
      Document doc = Jsoup.parse(pageSource);

      // Extract product information
      Elements products = doc.select(".product-item"); // Adjust selector based on actual HTML

      for (Element product : products) {
        String name = product.select(".product-name").text(); // Adjust selector
        String price = product.select(".product-price").text(); // Adjust selector

        System.out.println("Name: " + name);
        System.out.println("Price: " + price);
        System.out.println("---");
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      driver.quit();
    }
  }
}
