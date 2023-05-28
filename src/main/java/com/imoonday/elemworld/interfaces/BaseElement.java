package com.imoonday.elemworld.interfaces;

import com.imoonday.elemworld.elements.Element;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface BaseElement {

    Element getBaseElement();

    default void checkBaseElement(LivingEntity entity) {
        if (entity.getElements().stream().noneMatch(entry -> entry.element().isOf(this.getBaseElement()))) {
            entity.addElement(this.getBaseElement().withRandomLevel());
        }
    }

    default void checkBaseElement(ItemStack stack) {
        if (stack.getElements().stream().noneMatch(entry -> entry.element().isOf(this.getBaseElement()))) {
            stack.addElement(this.getBaseElement().withRandomLevel());
        }
    }
}
