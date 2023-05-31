package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class FireElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {

    public FireElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public FireElementalEnergyBallEntity(LivingEntity owner, ItemStack stack) {
        super(EWEntities.FIRE_ELEMENTAL_ENERGY_BALL, owner, stack);
    }

    @Override
    public Element getElement() {
        return EWElements.FIRE;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient && this.isTouchingWater()) {
            this.world.playSound(null, this.getBlockPos(), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.VOICE);
            this.discard();
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        this.world.createExplosion(getOwner(), this.getX(), this.getY(), this.getZ(), 3.0f, true, World.ExplosionSourceType.MOB);
        forEachLivingEntity(10, entity -> 7.0f, LivingEntity::isAlive, this::fireOrExplode, true);
    }

    private void fireOrExplode(LivingEntity entity) {
        if (entity.getRandom().nextFloat() < 0.25f) {
            if (entity.isOnFire()) {
                entity.world.createExplosion(getOwner(), entity.getX(), entity.getY(), entity.getZ(), 3.0f, World.ExplosionSourceType.NONE);
            } else {
                entity.setOnFireFor(10);
            }
        }
    }

}
