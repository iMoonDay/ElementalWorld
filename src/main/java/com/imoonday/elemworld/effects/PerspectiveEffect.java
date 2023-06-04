package com.imoonday.elemworld.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import java.awt.*;

public class PerspectiveEffect extends StatusEffect {
    public PerspectiveEffect() {
        super(StatusEffectCategory.BENEFICIAL, Color.WHITE.getRGB());
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(10 * (amplifier + 1)), entity1 -> entity1 instanceof LivingEntity living && living.isAlive() && !living.isSneaking())
                .stream().map(otherEntity -> (LivingEntity) otherEntity)
                .forEach(living -> living.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20 * (amplifier + 1), amplifier)));
    }
}
