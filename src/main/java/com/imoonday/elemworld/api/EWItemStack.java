package com.imoonday.elemworld.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public interface EWItemStack {

    default Set<Element.Entry> getElements() {
        return new HashSet<>();
    }

    default Set<Element.Entry> getStoredElementsIfBook() {
        return new HashSet<>();
    }

    default void setElements(Set<Element.Entry> entries) {

    }

    default ItemStack withElements(Set<Element.Entry> entries) {
        return null;
    }

    default boolean hasElement(Element element) {
        return false;
    }


    default boolean hasElement() {
        return false;
    }

    default boolean addElement(Element.Entry entry) {
        return false;
    }

    default void addStoredElementIfBook(Element.Entry entry) {
    }

    default void removeElement(Element element) {

    }

    default void removeInvalidElements() {

    }

    default Optional<Element.Entry> getElement(Element element) {
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
