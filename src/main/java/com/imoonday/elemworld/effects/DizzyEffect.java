package com.imoonday.elemworld.effects;

import com.imoonday.elemworld.init.EWEffects;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

import java.awt.*;

public class DizzyEffect extends StatusEffect {
    public DizzyEffect() {
        super(StatusEffectCategory.HARMFUL, Color.BLACK.getRGB());
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.isSpectator()) {
            return;
        }
        int i = (entity.world.getTime() % 100 <= 50 ? 1 : -1) * ++amplifier;
        float yaw = entity.getYaw() + i;
        while (yaw > 180) {
            yaw -= 360;
        }
        entity.setYaw(yaw);
        float headYaw = entity.getHeadYaw() + i;
        while (headYaw > 180) {
            headYaw -= 360;
        }
        entity.setHeadYaw(headYaw);
        float bodyYaw = entity.getBodyYaw() + i;
        while (bodyYaw > 180) {
            bodyYaw -= 360;
        }
        entity.setBodyYaw(bodyYaw);
        if (!entity.world.isClient) {
            for (ServerPlayerEntity player : PlayerLookup.tracking(entity)) {
                player.networkHandler.sendPacket(new EntitySetHeadYawS2CPacket(entity, (byte) entity.getHeadYaw()));
                player.networkHandler.sendPacket(new EntityPositionS2CPacket(entity));
            }
        }
    }

    public static class DizzyGoal extends Goal {

        private final MobEntity entity;
        private boolean dizzy;

        public DizzyGoal(MobEntity mob) {
            this.entity = mob;
        }

        @Override
        public boolean canStart() {
            return this.entity.hasStatusEffect(EWEffects.DIZZY);
        }

        @Override
        public void start() {
            this.dizzy = true;
        }

        @Override
        public void stop() {
            this.entity.setVelocity(0, 0, 0);
            this.dizzy = false;
        }

        @Override
        public void tick() {
            if (this.dizzy) {
                this.entity.getNavigation().stop();
                double speed = 0.05;
                StatusEffectInstance effect = this.entity.getStatusEffect(EWEffects.DIZZY);
                if (effect != null) {
                    speed = Math.min(speed * (effect.getAmplifier() + 1), 1);
                }
                this.entity.addVelocity(this.entity.getRandom().nextGaussian() * speed, 0, this.entity.getRandom().nextGaussian() * speed);
            }
        }
    }
}
