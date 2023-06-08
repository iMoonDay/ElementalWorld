package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class ThunderElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {

    public ThunderElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public ThunderElementalEnergyBallEntity(LivingEntity owner, ItemStack staffStack) {
        super(EWEntities.THUNDER_ELEMENTAL_ENERGY_BALL, owner, staffStack);
    }

    @Override
    public Element getElement() {
        return EWElements.THUNDER;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        forEachLivingEntity(0, entity -> spawnLightning(entity.getPos()), false);
        spawnLightningAndExplosion();
    }

    private void spawnLightning(Vec3d pos) {
        LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(this.world);
        if (lightningEntity != null) {
            lightningEntity.refreshPositionAfterTeleport(pos);
            lightningEntity.setChanneler(this.getOwner() instanceof ServerPlayerEntity serverPlayer ? serverPlayer : null);
            this.world.spawnEntity(lightningEntity);
        }
    }

    private void spawnLightningAndExplosion() {
        if (world.isClient) {
            return;
        }
        spawnLightning(this.getPos());
        for (int i = 0; i < this.random.nextBetween(4, 8); i++) {
            Vec3d pos = this.getPos().addRandom(random, 20.0f);
            spawnLightning(new Vec3d(pos.x, this.world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (int) pos.x, (int) pos.z), pos.z));
        }
        this.world.createExplosion(this.getOwner(), this.getX(), this.getY(), this.getZ(), 5.0f, true, World.ExplosionSourceType.TNT);
    }

    @Override
    protected void onSkyHeight() {
        if (world.isClient) {
            return;
        }
        for (int i = 0; i < this.random.nextBetween(10, 20); i++) {
            Vec3d pos = this.getPos().addRandom(random, 40.0f);
            spawnLightning(new Vec3d(pos.x, this.world.getTopY(Heightmap.Type.WORLD_SURFACE, (int) pos.x, (int) pos.z), pos.z));
        }
        this.discard();
    }
}
