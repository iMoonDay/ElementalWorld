package com.imoonday.elemworld.api;

import java.util.ArrayList;

public interface EWLivingEntity {
    default ArrayList<Element> getElements() {
        return null;
    }

    default boolean addElement(Element elements) {
        return false;
    }

    default void removeElement(Element elements) {

    }

    default void clearElements() {

    }

    default ArrayList<Element> getAllElements() {
        return null;
    }

    default boolean hasElement(Element element) {
        return false;
    }

    default boolean hasElementEffect(Element element) {
        return false;
    }

    default boolean isInElement(Element element) {
        return false;
    }
}
