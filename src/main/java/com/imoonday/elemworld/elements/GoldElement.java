package com.imoonday.elemworld.elements;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.init.EWEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.awt.*;

public class GoldElement extends Element {
    public GoldElement(int maxLevel, int rareLevel, int weight, float miningSpeedMultiplier, float damageMultiplier, float maxHealthMultiplier, float durabilityMultiplier) {
        super(maxLevel, rareLevel, weight, miningSpeedMultiplier, damageMultiplier, maxHealthMultiplier, durabilityMultiplier);
    }

    @Override
    public Color getColor() {
        return Color.ORANGE;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.decelerate(0.9);
    }

    @Override
    public void afterInjury(LivingEntity entity, DamageSource source, float amount) {
        Random random = entity.getRandom();
        Entity attacker = source.getAttacker();
        if (attacker instanceof LivingEntity living) {
            if (random.nextFloat() < 0.25f) {
                Vec3d vec3d = living.getPos().subtract(entity.getPos()).normalize();
                living.setVelocity(vec3d.x, 0.5, vec3d.z);
                if (living instanceof ServerPlayerEntity player) {
                    player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));
                }
            }
        }
    }

    @Override
    public void postHit(LivingEntity target, PlayerEntity attacker) {
        Random random = attacker.getRandom();
        if (random.nextFloat() < 0.25f) {
            target.addStatusEffect(new StatusEffectInstance(EWEffects.DIZZY, 10 * 20, 0));
        }
    }
}
