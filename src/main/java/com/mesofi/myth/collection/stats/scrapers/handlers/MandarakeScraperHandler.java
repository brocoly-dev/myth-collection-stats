package com.mesofi.myth.collection.stats.scrapers.handlers;

import com.mesofi.myth.collection.core.model.Store;
import com.mesofi.myth.collection.stats.scrapers.ScraperHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.jsoup.nodes.Element;

/** Mandarake Park scraper handler. */
public class MandarakeScraperHandler implements ScraperHandler {

  /** {@inheritDoc} */
  @Override
  public Store getStore() {
    return Store.MANDARAKE;
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchBaseUrl() {
    return "https://order.mandarake.co.jp";
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchContextUrl() {
    return "/order/listPage/list?dispAdult=0&soldOut=1&keyword=Myth%20cloth&lang=en&deviceId=1&page=";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurinesCssSelector() {
    return "div.block";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurineLinkCssSelector() {
    // Descendant: div with class "title" containing any descendant <a>
    return "div.title a";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurinePriceCssSelector() {
    // Descendant: div with class "price" containing any descendant <p>
    return "div.price p";
  }

  /** {@inheritDoc} */
  @Override
  public Map<String, String> customHeaders() {
    Map<String, String> headersMap = new HashMap<>();
    headersMap.put(
        "cookie",
        "tr_mndrk_user=ec4a30d2.637387b35f151; _ga=GA1.3.1827069113.1749565585; _gid=GA1.3.105185018.1749565585; _gat=1");
    return headersMap;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<String> getFigurineName(Element productLinkElement) {
    return Optional.of(productLinkElement.text());
  }
}
