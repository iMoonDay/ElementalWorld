package com.imoonday.elemworld.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public interface EWItemStack {

    default Set<ElementInstance> getElements() {
        return new HashSet<>();
    }

    default void setElements(Set<ElementInstance> instances) {

    }

    default ItemStack withElements(Set<ElementInstance> instances){
        return null;
    }

    default boolean hasElement(Element element) {
        return false;
    }


    default boolean addElement(ElementInstance instance) {
        return false;
    }

    default void removeElement(Element element) {

    }

    default Optional<ElementInstance> getElement(Element element) {
        return Optional.empty();
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
