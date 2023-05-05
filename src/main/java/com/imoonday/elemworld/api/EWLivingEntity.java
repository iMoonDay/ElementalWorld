package com.imoonday.elemworld.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface EWLivingEntity {

    default Set<ElementInstance> getElements() {
        return new HashSet<>();
    }

    default boolean addElement(ElementInstance instance) {
        return false;
    }

    default void removeElement(Element element) {

    }

    default void clearElements() {

    }

    default void setElements(Set<ElementInstance> instances) {

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

    default boolean hasNoElement() {
        return false;
    }

    default double getSpeed() {
        return 0;
    }

    default List<ElementInstance> getAllElements(boolean repeat) {
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
