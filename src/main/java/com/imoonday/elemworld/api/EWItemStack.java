package com.imoonday.elemworld.api;

import java.util.ArrayList;

public interface EWItemStack {
    default ArrayList<Element> getElements() {
        return null;
    }

    default void setElements(ArrayList<Element> elements) {

    }

    default boolean hasElement(Element element) {
        return false;
    }

    default boolean addElement(Element element) {
        return false;
    }

    default void removeElement(Element element) {

    }

    default void addRandomElements() {

    }

    default void addNewRandomElements(int count) {

    }
}
