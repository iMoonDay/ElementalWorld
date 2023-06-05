package com.imoonday.elemworld.elements;

import com.imoonday.elemworld.init.EWElements;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Map;

import static net.minecraft.entity.effect.StatusEffects.NIGHT_VISION;
import static net.minecraft.entity.effect.StatusEffects.SPEED;
import static net.minecraft.registry.tag.DamageTypeTags.IS_EXPLOSION;
import static net.minecraft.registry.tag.DamageTypeTags.IS_FIRE;

public class LightElement extends Element {
    public LightElement() {
        super(1, 3, 5);
    }

    @Override
    public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
        Float x = getMultiplier(world);
        if (x != null) return x;
        return super.getMiningSpeedMultiplier(world, entity, state);
    }

    @Override
    public float getDamageMultiplier(World world, LivingEntity entity, LivingEntity target) {
        Float x = getMultiplier(world);
        if (x != null) return x;
        return super.getDamageMultiplier(world, entity, target);
    }

    @Override
    public float getMaxHealthMultiplier(World world, LivingEntity entity) {
        Float x = getMultiplier(world);
        if (x != null) return x / 2.0f;
        return super.getMaxHealthMultiplier(world, entity);
    }

    @Nullable
    private Float getMultiplier(World world) {
        return world.isDay() ? 0.5f : null;
    }

    @Override
    public float getDamageProtectionMultiplier(DamageSource source, LivingEntity entity) {
        return source.isIn(IS_FIRE) || source.isIn(IS_EXPLOSION) ? 0.2f : 1.0f;
    }

    @Override
    public void postHit(LivingEntity target, PlayerEntity attacker) {
        if (target.isIn(EWElements.DARKNESS)) {
            float v = Random.create().nextFloat();
            double chance = 0.2 * (1 - (target.getHealth() / target.getMaxHealth()));
            if (v < chance) {
                if (!target.isInvulnerable()) {
                    target.world.playSound(null, target.getBlockPos(), SoundEvents.ITEM_TRIDENT_HIT, SoundCategory.VOICE);
                    target.kill();
                }
            }
        }
    }

    @Override
    public Map<StatusEffect, Integer> getPersistentEffects(Map<StatusEffect, Integer> effects) {
        effects.put(SPEED, 0);
        effects.put(NIGHT_VISION, 0);
        return effects;
    }

    @Override
    public Color getColor() {
        return Color.WHITE;
    }
}
