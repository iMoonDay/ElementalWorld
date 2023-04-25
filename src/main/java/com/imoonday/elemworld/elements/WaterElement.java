package com.imoonday.elemworld.elements;

import com.imoonday.elemworld.api.Element;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import static com.imoonday.elemworld.init.EWElements.EARTH;
import static com.imoonday.elemworld.init.EWElements.FIRE;

public class WaterElement extends Element {
    public WaterElement(int maxLevel, int rareLevel, int weight, float miningSpeedMultiplier, float damageMultiplier, float protectionMultiplier, float durabilityMultiplier) {
        super(maxLevel, rareLevel, weight, miningSpeedMultiplier, damageMultiplier, protectionMultiplier, durabilityMultiplier);
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
}
