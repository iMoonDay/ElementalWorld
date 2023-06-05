package com.imoonday.elemworld.effects;

import com.imoonday.elemworld.init.EWElements;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class LightFearingEffect extends StatusEffect {

    public LightFearingEffect() {
        super(StatusEffectCategory.HARMFUL, Color.WHITE.getRGB());
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.isAlive()) {
            boolean affected = isAffectedByDaylight(entity);
            if (affected) {
                ItemStack itemStack = entity.getEquippedStack(EquipmentSlot.HEAD);
                if (!itemStack.isEmpty()) {
                    if (itemStack.isDamageable()) {
                        itemStack.setDamage(itemStack.getDamage() + entity.getRandom().nextInt(2));
                        if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
                            entity.sendEquipmentBreakStatus(EquipmentSlot.HEAD);
                            entity.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }
                    affected = false;
                }
                if (affected) {
                    entity.setOnFireFor(4 * (amplifier + 1));
                }
            }
        }
    }

    public static boolean isAffectedByDaylight(LivingEntity entity) {
        if (entity instanceof PlayerEntity player && (player.isCreative() || player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE))) {
            return false;
        }
        if (entity.hasElement(EWElements.LIGHT)) {
            return false;
        }
        if (entity.world.isDay() && !entity.world.isClient) {
            float brightness = entity.getBrightnessAtEyes();
            BlockPos blockPos = BlockPos.ofFloored(entity.getX(), entity.getEyeY(), entity.getZ());
            boolean isWet = entity.isWet() || entity.inPowderSnow || entity.wasInPowderSnow;
            return brightness > 0.5f && entity.getRandom().nextFloat() * 30.0f < (brightness - 0.4f) * 2.0f && !isWet && entity.world.isSkyVisible(blockPos);
        }
        return false;
    }
}
