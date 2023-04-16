package com.imoonday.elemworld.api;

import java.util.ArrayList;

public interface EWItemStack {
    default ArrayList<Element> getElements() {
        return null;
    }

    default void setElements(ArrayList<Element> elements) {

    }

    default boolean addElement(Element element) {
        return false;
    }
}
