package com.imoonday.elemworld.api;

import net.minecraft.entity.LivingEntity;
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
