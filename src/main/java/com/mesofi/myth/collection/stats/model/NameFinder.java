package com.mesofi.myth.collection.stats.model;

import com.mesofi.myth.collection.core.model.Category;
import com.mesofi.myth.collection.core.model.LineUp;

public record NameFinder(String targetName, LineUp lineUp, Category category, boolean oce) {}
