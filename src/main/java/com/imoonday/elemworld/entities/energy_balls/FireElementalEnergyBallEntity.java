package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
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
        long count = BlockPos.stream(this.getBoundingBox().expand(1))
                .filter(pos -> !this.world.getBlockState(pos).getFluidState().isEmpty())
                .peek(pos -> this.world.setBlockState(pos, Blocks.AIR.getDefaultState()))
                .count();
        if (count > 0) {
            this.world.playSound(null, this.getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.VOICE);
        }

        BlockPos.stream(this.getBoundingBox().expand(0, -1, 0))
                .filter(pos -> this.world.getBlockState(pos).isAir() && this.world.getBlockState(pos.down()).isOpaqueFullCube(this.world, pos.down()))
                .forEach(pos -> this.world.setBlockState(pos, Blocks.FIRE.getDefaultState()));
    }

    @Override
    public boolean isOnFire() {
        return true;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        forEachLivingEntity(7.0f, this::fireOrExplode, true);
        explode();
    }

    private void explode() {
        if (!world.isClient) {
            this.world.createExplosion(getOwner(), this.getX(), this.getY(), this.getZ(), 3.0f, true, World.ExplosionSourceType.MOB);
        }
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
