package com.imoonday.elemworld.api;

import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface EWLivingEntity {

    default Map<Element, Integer> getElements() {
        return new HashMap<>();
    }

    default boolean addElement(Element element, int level) {
        return false;
    }

    default void removeElement(Element element) {

    }

    default boolean addElement(Pair<Element, Integer> pair) {
        return false;
    }

    default void clearElements() {

    }

    default boolean hasSpace() {
        return false;
    }

    default void setElements(Map<Element, Integer> elements) {

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

    default List<Pair<Element, Integer>> getAllElements(boolean repeat) {
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
