package com.imoonday.elemworld.api;

import java.util.ArrayList;

public interface EWLivingEntity {

    default ArrayList<Element> getElements() {
        return new ArrayList<>();
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
        return new ArrayList<>();
    }

    default boolean hasElement(Element element) {
        return false;
    }

   default boolean hasOneOf(Element... elements) {
        return false;
    }

    default boolean hasEffectOf(Element element) {
        return false;
    }

    default void removeEffectOf(Element element) {

    }

    default boolean isIn(Element element) {
        return false;
    }

    default void decelerate(double multiplier) {

    }

    default boolean isHolding(Element element) {
        return false;
    }
}
