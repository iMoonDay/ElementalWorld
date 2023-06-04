package com.imoonday.elemworld.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

import java.awt.*;

public class RobberyEffect extends StatusEffect {
    public RobberyEffect() {
        super(StatusEffectCategory.BENEFICIAL, Color.YELLOW.getRGB());
    }
}
