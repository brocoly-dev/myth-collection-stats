package com.mesofi.myth.collection.stats.scrapers.handlers;

import com.mesofi.myth.collection.core.model.Store;
import com.mesofi.myth.collection.stats.scrapers.ScraperHandler;

/** Yoyakunow scraper handler. */
public class YoyakunowScraperHandler implements ScraperHandler {

  /** {@inheritDoc} */
  @Override
  public Store getStore() {
    return Store.YOYAKUNOW;
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchBaseUrl() {
    // TODO Auto-generated method stub
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchContextUrl() {
    // TODO Auto-generated method stub
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurinesCssSelector() {
    // TODO Auto-generated method stub
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurineLinkCssSelector() {
    // TODO Auto-generated method stub
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurinePriceCssSelector() {
    // TODO Auto-generated method stub
    return null;
  }
}
