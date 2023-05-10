package com.imoonday.elemworld.api;

import java.util.HashMap;
import java.util.Map;

public interface EWRegistry {

    /**
     * @param map String -> Name
     */
    void registerElements(Map<String, Element> map);

    static Map<String, Element> getRegisterElements(EWRegistry registry) {
        Map<String, Element> map = new HashMap<>();
        registry.registerElements(map);
        return map;
    }

}
