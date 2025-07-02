package com.mesofi.myth.collection.stats.model;

import com.mesofi.myth.collection.core.model.Anniversary;
import com.mesofi.myth.collection.core.model.Category;
import com.mesofi.myth.collection.core.model.LineUp;
import com.mesofi.myth.collection.core.model.Series;
import java.util.List;

public record AttributeExtractionResult(
    String filteredName,
    LineUp lineUp,
    List<Category> categoryList,
    Series series,
    boolean attribute,
    Anniversary anniversary) {}
