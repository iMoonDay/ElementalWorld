package com.imoonday.elemworld.effects;

import com.imoonday.elemworld.init.EWEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

import java.awt.*;

public class FreezingResistanceEffect extends StatusEffect {
    public FreezingResistanceEffect() {
        super(StatusEffectCategory.BENEFICIAL, Color.CYAN.getRGB());
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.removeStatusEffect(EWEffects.FREEZE);
    }
}
