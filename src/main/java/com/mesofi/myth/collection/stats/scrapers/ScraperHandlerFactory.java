package com.mesofi.myth.collection.stats.scrapers;

import com.mesofi.myth.collection.core.model.Store;
import com.mesofi.myth.collection.stats.scrapers.handlers.*;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ScraperHandlerFactory {

  private final Map<Store, ScraperHandler> handlers;

  public ScraperHandlerFactory() {
    handlers = new HashMap<>();
    handlers.put(Store.LUNA_PARK, new LunaParkScraperHandler());
    handlers.put(Store.NIN_NIN_GAME, new NinNinGameScraperHandler());
    handlers.put(Store.YOYAKUNOW, new YoyakunowScraperHandler());
    handlers.put(Store.MANDARAKE, new MandarakeScraperHandler());
    handlers.put(Store.HOBBY_GENKI, new HobbyGenkiScraperHandler());
  }

  public ScraperHandler getHandler(Store store) {
    return handlers.get(store);
  }
}
