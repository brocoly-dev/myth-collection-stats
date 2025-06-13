package com.mesofi.myth.collection.stats;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v135.network.Network;
import org.openqa.selenium.devtools.v135.network.model.Headers;

public class MandarakeScraper {
  public static void main(String[] args) {
    // Setup ChromeDriver
    WebDriverManager.chromedriver().setup();

    ChromeOptions options = new ChromeOptions();
    // options.addArguments("--headless=new");

    ChromeDriver driver = new ChromeDriver(options);
    DevTools devTools = driver.getDevTools();
    devTools.createSession();

    Map<String, Object> headersMap = new HashMap<>();
    headersMap.put(
        "accept",
        "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
    headersMap.put("accept-encoding", "gzip, deflate, br, zstd");
    headersMap.put("accept-language", "en-US,en;q=0.9");
    headersMap.put(
        "cookie",
        "tr_mndrk_user=ec4a30d2.637387b35f151; _ga=GA1.3.1827069113.1749565585; _gid=GA1.3.105185018.1749565585; _gat=1");
    headersMap.put("priority", "u=0, i");
    headersMap.put("referer", "https://www.mandarake.co.jp/");
    headersMap.put(
        "sec-ch-ua",
        "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"");
    headersMap.put("sec-ch-ua-mobile", "?0");
    headersMap.put("sec-ch-ua-platform", "\"macOS\"");
    headersMap.put("sec-fetch-dest", "document");
    headersMap.put("sec-fetch-mode", "navigate");
    headersMap.put("sec-fetch-site", "same-site");
    headersMap.put("sec-fetch-user", "?1");
    headersMap.put(
        "user-agent",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36");

    Headers headers = new Headers(headersMap);
    devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
    devTools.send(Network.setExtraHTTPHeaders(headers));

    try {
      // Load the Mandarake search results page
      // String url =
      // "https://order.mandarake.co.jp/order/listPage/list?dispAdult=0&soldOut=1&keyword=Myth%20cloth&lang=en&deviceId=1&page=1";
      String url =
          "https://order.mandarake.co.jp/order/listPage/list?dispAdult=0&soldOut=1&keyword=Myth%20cloth&lang=en&deviceId=1&page=1";
      driver.get(url);

      // Wait a few seconds for page to load (or use WebDriverWait for better reliability)
      Thread.sleep(6000);
      // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

      // wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return
      // document.readyState").equals("complete"));

      // Now navigate to the search URL
      // String searchUrl =
      // "https://order.mandarake.co.jp/order/listPage/list?keyword=myth%20cloth&lang=en";
      // driver.get(searchUrl);

      // Wait for the search results page to load
      // wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return
      // document.readyState").equals("complete"));

      // Get all product elements
      List<WebElement> items = driver.findElements(By.cssSelector(".list-item"));

      // Loop through each product
      for (WebElement item : items) {
        // Get the title
        String title = item.findElement(By.className("title")).getText();

        // Get the price
        String price = item.findElement(By.className("price")).getText();

        System.out.println("Title: " + title);
        System.out.println("Price: " + price);
        System.out.println("------");
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // Always quit the driver
      driver.quit();
    }
  }
}
