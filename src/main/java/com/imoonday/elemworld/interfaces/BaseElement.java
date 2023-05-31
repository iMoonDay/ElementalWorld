package com.imoonday.elemworld.interfaces;

import com.imoonday.elemworld.elements.Element;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface BaseElement {

    Element getBaseElement();

    default void checkBaseElement(LivingEntity entity) {
        if (!entity.hasElement(this.getBaseElement())) {
            entity.addElement(this.getBaseElement().withRandomLevel());
        }
    }

    default void checkBaseElement(ItemStack stack) {
        if (!stack.hasElement(this.getBaseElement())) {
            stack.addElement(this.getBaseElement().withRandomLevel());
        }
    }
}
