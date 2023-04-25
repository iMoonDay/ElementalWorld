package com.imoonday.elemworld.elements;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.init.EWEffects;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.ArrayList;

import static com.imoonday.elemworld.init.EWElements.EARTH;
import static com.imoonday.elemworld.init.EWElements.WATER;

public class FireElement extends Element {
    public FireElement(int maxLevel, int rareLevel, int weight, float miningSpeedMultiplier, float damageMultiplier, float protectionMultiplier, float durabilityMultiplier) {
        super(maxLevel, rareLevel, weight, miningSpeedMultiplier, damageMultiplier, protectionMultiplier, durabilityMultiplier);
    }

    @Override
    public float getExtraDamage(LivingEntity target, float amount) {
        if (target.isIn(WATER)) {
            return Math.max(-amount * 0.25f, -20);
        }
        return super.getExtraDamage(target, amount);
    }

    @Override
    public void postHit(LivingEntity target, PlayerEntity attacker) {
        if (target.isIn(WATER)) {
            StatusEffectInstance effect = target.getStatusEffect(WATER.getEffect());
            if (effect != null) {
                int duration = effect.getDuration() - 3 * 20;
                if (duration <= 0) {
                    target.removeEffectOf(WATER);
                } else {
                    target.setStatusEffect(new StatusEffectInstance(WATER.getEffect(), duration, effect.getAmplifier(), effect.isAmbient(), effect.shouldShowParticles(), effect.shouldShowIcon()), attacker);
                }
            }
        }
    }

    @Override
    public int getEffectTime(LivingEntity target) {
        if (target.hasElement(EARTH)) {
            return 3;
        }
        return super.getEffectTime(target);
    }

    @Override
    public boolean shouldAddEffect(LivingEntity entity) {
        return entity.isOnFire() || entity.isInLava();
    }

    @Override
    public void tick(LivingEntity entity) {
        if (!entity.hasStatusEffect(EWEffects.FREEZE)) {
            return;
        }
        entity.removeStatusEffect(EWEffects.FREEZE);
        entity.world.playSound(null, entity.getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.VOICE, 1, 1);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.hasStatusEffect(EWEffects.FREEZE)) {
            return;
        }
        entity.removeStatusEffect(EWEffects.FREEZE);
        entity.world.playSound(null, entity.getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.VOICE, 1, 1);
        if (!entity.world.isClient) {
            ArrayList<ServerPlayerEntity> entities = new ArrayList<>(PlayerLookup.tracking(entity));
            if (entity instanceof ServerPlayerEntity player) {
                entities.add(player);
            }
            for (ServerPlayerEntity player : entities) {
                player.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(player.getId(), EWEffects.FREEZE));
            }
        }
    }
}
