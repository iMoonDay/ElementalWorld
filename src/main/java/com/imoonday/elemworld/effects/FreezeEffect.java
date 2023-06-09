package com.imoonday.elemworld.effects;

import com.imoonday.elemworld.init.EWEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import java.awt.*;

public class FreezeEffect extends StatusEffect {
    public FreezeEffect() {
        super(StatusEffectCategory.HARMFUL, Color.CYAN.getRGB());
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.isSpectator() && !entity.hasStatusEffect(EWEffects.FREEZING_RESISTANCE)) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 2, 5));
        }
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);
        if (!entity.hasStatusEffect(EWEffects.FREEZING_RESISTANCE)) {
            entity.addStatusEffect(new StatusEffectInstance(EWEffects.FREEZING_RESISTANCE, 10 * 20, amplifier, false, false, false));
        }
    }
}
