package com.imoonday.elemworld.elements;

import com.imoonday.elemworld.api.Element;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.awt.*;

import static com.imoonday.elemworld.init.EWElements.EARTH;
import static com.imoonday.elemworld.init.EWElements.FIRE;

public class WaterElement extends Element {
    public WaterElement() {
        super(3, 1, 40, 0.0f, 0.25f, 0.0f, 0.0f);
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
    public float getExtraDamage(LivingEntity target, float amount) {
        if (target.isIn(FIRE)) {
            return 2.0f;
        }
        return super.getExtraDamage(target, amount);
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
        return entity.isWet();
    }

    @Override
    public float getDamageProtectionMultiplier(DamageSource source, LivingEntity entity) {
        return source.isIn(DamageTypeTags.IS_DROWNING) ? 0.5f : super.getDamageProtectionMultiplier(source, entity);
    }

    @Override
    public Color getColor() {
        return Color.CYAN;
    }
}
