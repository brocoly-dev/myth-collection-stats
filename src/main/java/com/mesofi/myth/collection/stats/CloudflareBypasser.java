package com.mesofi.myth.collection.stats;

import java.io.IOException;
import java.util.Random;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CloudflareBypasser {

  private static final String[] USER_AGENTS = {
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:121.0) Gecko/20100101 Firefox/121.0",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.1 Safari/605.1.15"
  };

  public static Document fetchWithCloudflareBypass(String url)
      throws IOException, InterruptedException {
    Random random = new Random();

    // Add random delay to appear more human-like
    Thread.sleep(1000 + random.nextInt(2000));

    Connection connection =
        Jsoup.connect(url)
            .userAgent(USER_AGENTS[random.nextInt(USER_AGENTS.length)])
            .header(
                "Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
            .header("Accept-Language", "en-US,en;q=0.9")
            .header("Accept-Encoding", "gzip, deflate, br")
            .header("DNT", "1")
            .header("Connection", "keep-alive")
            .header("Upgrade-Insecure-Requests", "1")
            .header("Sec-Fetch-Dest", "document")
            .header("Sec-Fetch-Mode", "navigate")
            .header("Sec-Fetch-Site", "none")
            .header("Cache-Control", "max-age=0")
            .referrer("https://www.google.com/")
            .timeout(30000)
            .followRedirects(true)
            .ignoreHttpErrors(false);

    try {
      return connection.get();
    } catch (IOException e) {
      System.err.println("First attempt failed: " + e.getMessage());

      // Retry with different approach
      Thread.sleep(3000 + random.nextInt(2000));

      return Jsoup.connect(url)
          .userAgent(USER_AGENTS[random.nextInt(USER_AGENTS.length)])
          .header("Accept", "*/*")
          .referrer("https://www.yoyakunow.com/en/search?controller=search&s=myth+cloth&page=1")
          .timeout(30000)
          .followRedirects(true)
          .ignoreHttpErrors(true)
          .get();
    }
  }

  public static void main(String[] args) {
    String url = "https://www.yoyakunow.com/en/search?controller=search&s=myth+cloth&page=1";

    try {
      Document doc = fetchWithCloudflareBypass(url);
      System.out.println("Successfully fetched page. Title: " + doc.title());

      // Your scraping logic here
      scrapeProducts(doc);

    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static void scrapeProducts(Document doc) {
    // Your existing scraping logic
    Elements products = doc.select(".product-item, .product, .item");
    System.out.println("Found " + products.size() + " product elements");

    for (Element product : products) {
      String name = product.select("h2 a, h3 a, .product-name").text();
      String price = product.select(".price, .product-price").text();

      if (!name.isEmpty() || !price.isEmpty()) {
        System.out.println("Name: " + name);
        System.out.println("Price: " + price);
        System.out.println("---");
      }
    }
  }
}
