package com.imoonday.elemworld.api;

import com.imoonday.elemworld.elements.Element;

import java.util.HashMap;
import java.util.Map;

public interface EWRegistry {

    /**
     * @param map String -> Name
     */
    void registerElements(Map<String, Translation<Element>> map);

    static Map<String, Translation<Element>> getRegisterElements(EWRegistry registry) {
        Map<String, Translation<Element>> map = new HashMap<>();
        registry.registerElements(map);
        return map;
    }

}
