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

    default boolean hasSpace() {
        return false;
    }

    default void setElements(ArrayList<Element> elements) {

    }

    default int getHealTick() {
        return 0;
    }

    default void setHealTick(int healTick) {

    }

    default int getImmuneCooldown() {
        return 0;
    }

    default void setImmuneCooldown(int immuneCooldown) {

    }

    default double getSpeed() {
        return 0;
    }

    default ArrayList<Element> getAllElements() {
        return null;
    }

    default boolean hasElement(Element element) {
        return false;
    }

    default boolean hasOneOfElements(Element... elements) {
        return false;
    }

    default boolean hasElementEffect(Element element) {
        return false;
    }

    default void removeElementEffect(Element element) {

    }

    default boolean isInElement(Element element) {
        return false;
    }
}
