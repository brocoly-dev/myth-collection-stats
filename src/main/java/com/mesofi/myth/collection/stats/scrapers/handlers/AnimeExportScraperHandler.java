package com.mesofi.myth.collection.stats.scrapers.handlers;

import com.mesofi.myth.collection.core.model.Store;
import com.mesofi.myth.collection.stats.scrapers.ScraperHandler;
import java.util.Optional;
import org.jsoup.nodes.Element;

/** Anime Export scraper handler. */
public class AnimeExportScraperHandler implements ScraperHandler {

  /** {@inheritDoc} */
  @Override
  public Store getStore() {
    return Store.ANIME_EXPORT;
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchBaseUrl() {
    return "https://www.anime-export.com";
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchContextUrl() {
    return "/index.php?ptype=all&ipp=52&searchbox=myth%20cloth&category=&tag=&manufacturer=&deadlinelimit=&parentid=&pnum=";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurinesCssSelector() {
    return "div.col-xs-6.col-md-3";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurineLinkCssSelector() {
    return "a";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurinePriceCssSelector() {
    return "span.spanprice1";
  }

  /** {@inheritDoc} */
  @Override
  public int initialPageNumber() {
    return 0;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<String> getFigurineName(Element productLinkElement) {
    return Optional.ofNullable(productLinkElement.selectFirst("div.indexproductcardinfoup p"))
        .map(Element::text);
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurineAvailabilityCssSelector() {
    return "span:contains(IN STOCK)";
  }
}
