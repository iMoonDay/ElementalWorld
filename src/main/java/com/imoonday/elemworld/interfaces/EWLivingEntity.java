package com.imoonday.elemworld.interfaces;

import com.imoonday.elemworld.elements.Element;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public interface EWLivingEntity {

    default Set<Element.Entry> getElements() {
        return new HashSet<>();
    }

    default Set<Element.Entry> getEffectElements() {
        return null;
    }

    default boolean addElement(Element.Entry entry) {
        return false;
    }

    default void removeElement(Element element) {

    }

    default void clearElements() {

    }

    default void setElements(Set<Element.Entry> entries) {

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

    default boolean dropElementFragmentRandomly() {
        return true;
    }

    default Map<Long, Vec3d> getPosHistory() {
        return new HashMap<>();
    }

    default double getSpeed() {
        return 0;
    }

    default List<Element.Entry> getAllElements(boolean repeat) {
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

    default boolean hasAllElements() {
        return false;
    }

    default boolean isInFreeze() {
        return false;
    }

    default void setInFreeze(boolean inFreeze) {

    }

    default boolean isImmuneFallDamage() {
        return false;
    }

    default void setImmuneFallDamage(boolean immuneFallDamage) {

    }
}
