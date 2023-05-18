package com.imoonday.elemworld.effects;

import com.imoonday.elemworld.init.EWElements;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

import java.awt.*;

public class WaterFearingEffect extends StatusEffect {

    public WaterFearingEffect() {
        super(StatusEffectCategory.HARMFUL, Color.RED.getRGB());
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.isWet() || entity.hasEffectOf(EWElements.WATER)) {
            entity.damage(entity.getDamageSources().magic(), (amplifier + 1) * 0.3f);
        }
    }
}
