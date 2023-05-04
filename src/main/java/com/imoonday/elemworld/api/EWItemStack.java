package com.imoonday.elemworld.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.Map;

public interface EWItemStack {

    default Map<Element, Integer> getElements() {
        return new HashMap<>();
    }

    default void setElements(Map<Element, Integer> elements) {

    }

    default boolean hasElement(Element element) {
        return false;
    }

    default boolean addElement(Element element, int level) {
        return false;
    }

    default boolean addElement(Pair<Element, Integer> pair) {
        return false;
    }

    default void removeElement(Element element) {

    }

    default boolean hasSuitableElement() {
        return false;
    }

    default float getDamageMultiplier(LivingEntity entity) {
        return 1.0f;
    }

    default float getMaxHealthMultiplier(LivingEntity entity) {
        return 1.0f;
    }

    default float getMiningSpeedMultiplier(LivingEntity entity) {
        return 1.0f;
    }

    default float getDurabilityMultiplier() {
        return 1.0f;
    }

    default void addRandomElements() {

    }

    default void addNewRandomElement() {

    }
}
