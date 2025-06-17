package com.mesofi.myth.collection.stats.scrapers.handlers;

import com.mesofi.myth.collection.core.model.Store;
import com.mesofi.myth.collection.stats.scrapers.ScraperHandler;
import java.util.Optional;
import org.jsoup.nodes.Element;

/** Mykombini scraper handler. */
public class MykombiniScraperHandler implements ScraperHandler {

  /** {@inheritDoc} */
  @Override
  public Store getStore() {
    return Store.MYKOMBINI;
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchBaseUrl() {
    return "https://mykombini.com";
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchContextUrl() {
    return "/en/Research?orderby=position&orderway=desc&search_query=myth+cloth&submit_search=OK&p=";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurinesCssSelector() {
    return "li.ajax_block_product";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurineLinkCssSelector() {
    return "h3 a";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurinePriceCssSelector() {
    return "p.price_container span.price";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurineAvailabilityCssSelector() {
    return "div a.exclusive.ajax_add_to_cart_button";
  }

  /** {@inheritDoc} */
  @Override
  public Optional<String> getFigurineName(Element productLinkElement) {
    return Optional.of(productLinkElement.attr("title"));
  }
}
