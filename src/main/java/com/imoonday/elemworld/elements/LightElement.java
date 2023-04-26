package com.imoonday.elemworld.elements;

import com.imoonday.elemworld.api.Element;
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

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.entity.effect.StatusEffects.NIGHT_VISION;
import static net.minecraft.entity.effect.StatusEffects.SPEED;
import static net.minecraft.registry.tag.DamageTypeTags.IS_EXPLOSION;
import static net.minecraft.registry.tag.DamageTypeTags.IS_FIRE;

public class LightElement extends Element {
    public LightElement(int maxLevel, int rareLevel, int weight) {
        super(maxLevel, rareLevel, weight);
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
    public float getProtectionMultiplier(World world, LivingEntity entity) {
        Float x = getMultiplier(world);
        if (x != null) return x;
        return super.getProtectionMultiplier(world, entity);
    }

    @Nullable
    private Float getMultiplier(World world) {
        long time = world.getTimeOfDay();
        if (time >= 1000 && time < 13000) {
            return 1.5f;
        }
        return null;
    }

    @Override
    public boolean ignoreDamage(DamageSource source, LivingEntity entity) {
        return source.isIn(IS_FIRE) || source.isIn(IS_EXPLOSION);
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
    public Map<StatusEffect, Integer> getPersistentEffects() {
        Map<StatusEffect, Integer> effects = new HashMap<>();
        effects.put(SPEED, 0);
        effects.put(NIGHT_VISION, 0);
        return effects;
    }
}
