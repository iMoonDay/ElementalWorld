package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

public class GoldElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {

    public GoldElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public GoldElementalEnergyBallEntity(LivingEntity owner, ItemStack staffStack) {
        super(EWEntities.GOLD_ELEMENTAL_ENERGY_BALL, owner, staffStack);
    }

    @Override
    public Element getElement() {
        return EWElements.GOLD;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        forEachLivingEntity(5.0f, this::spawnParticlesAndAddEffects, false);
    }

    private void spawnParticlesAndAddEffects(LivingEntity entity) {
        spawnParticlesAtEntity(entity);
        Vec3d vec3d = entity.getPos().subtract(this.getPos()).normalize().multiply(2);
        entity.setVelocity(vec3d.x, 1, vec3d.z);
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 10 * 20));
    }

    public static void spawnParticlesAtEntity(LivingEntity entity) {
        Vec3d pos = entity.getBoundingBox().getCenter();
        World world = entity.world;
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        double step = 0.1;
        for (double i = step; i <= 3.5; i += step) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        spawnParticles(x + i * dx, y + i * dy, z + i * dz, world);
                    }
                }
            }
        }
    }

    public static void spawnParticles(double x, double y, double z, World world) {
        DustParticleEffect particle = new DustParticleEffect(Vec3d.unpackRgb(Color.YELLOW.getRGB()).toVector3f(), 255);
        if (world instanceof ClientWorld clientWorld) {
            clientWorld.addParticle(particle, x, y, z, 0, 0, 0);
        } else if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(particle, x, y, z, 1, 0, 0, 0, 0);
        }
    }
}
