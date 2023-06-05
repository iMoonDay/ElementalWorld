package com.imoonday.elemworld.elements;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Map;

import static com.imoonday.elemworld.init.EWElements.LIGHT;
import static net.minecraft.entity.damage.DamageTypes.*;
import static net.minecraft.entity.effect.StatusEffects.SPEED;

public class DarknessElement extends Element {
    public DarknessElement() {
        super(1, 3, 5);
    }

    @Override
    public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
        Float x = getMultiplier(world, entity);
        if (x != null) return x;
        return super.getMiningSpeedMultiplier(world, entity, state);
    }

    @Override
    public float getDamageMultiplier(World world, LivingEntity entity, LivingEntity target) {
        Float x = getMultiplier(world, entity);
        if (x != null) return x;
        return super.getDamageMultiplier(world, entity, target);
    }

    @Override
    public float getMaxHealthMultiplier(World world, LivingEntity entity) {
        Float x = getMultiplier(world, entity);
        if (x != null) return x / 5.0f;
        return super.getMaxHealthMultiplier(world, entity);
    }

    @Nullable
    private Float getMultiplier(World world, LivingEntity entity) {
        return world.isNight() ? hasNoLight(world, entity) ? 2.0f : 1.0f : null;
    }

    private static boolean hasNoLight(World world, LivingEntity entity) {
        BlockPos pos = entity.getBlockPos();
        return world.getLightLevel(pos) == 0;
    }

    @Override
    public float getDamageProtectionMultiplier(DamageSource source, LivingEntity entity) {
        if (source.isIndirect()) {
            return 1.0f;
        }
        if (source.getAttacker() instanceof LivingEntity attacker) {
            long time = entity.world.getTimeOfDay();
            if (attacker.getMainHandStack().hasElement(LIGHT) || attacker.isIn(LIGHT) || time >= 1000 && time < 13000) {
                return 1.0f;
            }
        }
        return source.isOf(MOB_ATTACK) || source.isOf(MOB_ATTACK_NO_AGGRO) || source.isOf(PLAYER_ATTACK) ? 0.2f : 1.0f;
    }

    @Override
    public Map<StatusEffect, Integer> getPersistentEffects(Map<StatusEffect, Integer> effects) {
        effects.put(SPEED, 0);
        return effects;
    }

    @Override
    public Color getColor() {
        return Color.BLACK;
    }

    @Override
    public boolean shouldAddEffect(LivingEntity entity) {
        return hasNoLight(entity.world, entity) && !entity.world.isClient;
    }
}
