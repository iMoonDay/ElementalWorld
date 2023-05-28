package com.imoonday.elemworld.interfaces;

import java.util.Map;

public interface Translatable {

    String getDefaultTranslation();

    default String getChineseTranslation() {
        return null;
    }

    default Map<String, String> getOtherTranslations(Map<String, String> map) {
        return map;
    }
}
