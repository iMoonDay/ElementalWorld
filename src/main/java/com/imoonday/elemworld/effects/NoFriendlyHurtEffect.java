package com.imoonday.elemworld.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

import java.awt.*;

public class NoFriendlyHurtEffect extends StatusEffect {
    public NoFriendlyHurtEffect() {
        super(StatusEffectCategory.BENEFICIAL, Color.GREEN.getRGB());
    }
}
