package com.imoonday.elemworld.elements;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.init.EWEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.Map;
import java.util.function.Predicate;

import static com.imoonday.elemworld.init.EWElements.FIRE;
import static com.imoonday.elemworld.init.EWElements.WATER;

public class IceElement extends Element {
    public IceElement(int maxLevel, int rareLevel, int weight, float miningSpeedMultiplier, float damageMultiplier, float protectionMultiplier, float durabilityMultiplier) {
        super(maxLevel, rareLevel, weight, miningSpeedMultiplier, damageMultiplier, protectionMultiplier, durabilityMultiplier);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.isIn(WATER)) {
            if (entity.hasStatusEffect(EWEffects.FREEZE)) {
                return;
            }
            entity.addStatusEffect(new StatusEffectInstance(EWEffects.FREEZE, 1, 0, false, false, false));
        } else {
            entity.decelerate(0.5);
        }
    }

    @Override
    public int getEffectTime(LivingEntity target) {
        if (target.hasStatusEffect(EWEffects.FREEZING_RESISTANCE)) {
            return 0;
        }
        return super.getEffectTime(target);
    }

    @Override
    public void postHit(LivingEntity target, PlayerEntity attacker) {
        if (target.isIn(FIRE)) {
            target.removeEffectOf(FIRE);
            if (target.isOnFire()) {
                target.setOnFire(false);
            }
            World world = target.world;
            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.LARGE_SMOKE, target.getX(), target.getY() + target.getHeight(), target.getZ(), 8, 0.5, 0.25, 0.5, 0.0);
                serverWorld.playSound(null, target.getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (world.random.nextFloat() - world.random.nextFloat()) * 0.8f);
            }
        }
    }

    @Override
    public void writeDamageMultiplier(Map<Predicate<LivingEntity>, Float> map) {
        map.put(living -> living.hasStatusEffect(EWEffects.FREEZE), 1.5f);
    }

    @Override
    public float getExtraDamage(LivingEntity target, float amount) {
        if (target.isIn(FIRE)) {
            return 3.0f;
        }
        return super.getExtraDamage(target, amount);
    }
}
