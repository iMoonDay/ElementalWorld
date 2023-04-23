package com.imoonday.elemworld.api;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public interface EWItemStack {
    @NotNull
    default ArrayList<Element> getElements() {
        return new ArrayList<>();
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
