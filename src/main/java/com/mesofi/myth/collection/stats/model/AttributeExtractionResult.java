package com.mesofi.myth.collection.stats.model;

import com.mesofi.myth.collection.core.model.Anniversary;
import com.mesofi.myth.collection.core.model.Category;
import com.mesofi.myth.collection.core.model.LineUp;
import java.util.List;

public record AttributeExtractionResult(
    String filteredName,
    LineUp lineUp,
    List<Category> categoryList,
    boolean attribute,
    Anniversary anniversary) {}
