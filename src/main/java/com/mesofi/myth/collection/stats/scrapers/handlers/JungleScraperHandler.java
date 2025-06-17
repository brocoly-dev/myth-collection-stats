package com.mesofi.myth.collection.stats.scrapers.handlers;

import com.mesofi.myth.collection.core.model.Store;
import com.mesofi.myth.collection.stats.scrapers.ScraperHandler;
import java.util.Optional;
import org.jsoup.nodes.Element;

/** Hobby Link Japan handler. */
public class JungleScraperHandler implements ScraperHandler {

  /** {@inheritDoc} */
  @Override
  public Store getStore() {
    return Store.JUNGLE;
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchBaseUrl() {
    return "https://jungle-scs-ensale.com";
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchContextUrl() {
    return "/products/list?category_id=&rank=&orderby=&name=myth%20cloth&pageno=";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurinesCssSelector() {
    return "li.ec-shelfGrid__item";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurineLinkCssSelector() {
    return "div.ec-productItemRole__image a";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurinePriceCssSelector() {
    return "span.ec-price__price";
  }

  /** {@inheritDoc} */
  @Override
  public Optional<String> getFigurineName(Element productLinkElement) {
    if (productLinkElement.childNodeSize() == 0) {
      return Optional.empty();
    }
    Element imgElement = productLinkElement.child(0);
    return Optional.of(imgElement.attr("alt"));
  }
}
