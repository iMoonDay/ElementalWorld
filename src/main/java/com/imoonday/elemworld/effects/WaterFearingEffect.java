package com.imoonday.elemworld.effects;

import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.math.BlockPos;

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
            if (isBeingRainedOn(entity) && !entity.isTouchingWater()) {
                if (entity.getEquippedStack(EquipmentSlot.HEAD).isOf(EWItems.UMBRELLA)) {
                    return;
                }
                if (entity.isUsingItem() && entity.getActiveItem().isOf(EWItems.UMBRELLA)) {
                    return;
                }
            }
            entity.damage(entity.getDamageSources().magic(), (amplifier + 1) * 0.3f);
        }
    }

    private static boolean isBeingRainedOn(LivingEntity entity) {
        BlockPos blockPos = entity.getBlockPos();
        return entity.world.hasRain(blockPos) || entity.world.hasRain(BlockPos.ofFloored(blockPos.getX(), entity.getBoundingBox().maxY, blockPos.getZ()));
    }
}
