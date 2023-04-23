package com.imoonday.elemworld.api;

import net.minecraft.entity.effect.StatusEffectInstance;

public interface EWStatusEffectInstance {
    default StatusEffectInstance setFromElement(boolean fromElement) {
        return null;
    }

    default boolean isFromElement() {
        return false;
    }
}
