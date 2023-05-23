package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class EarthElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {
    public EarthElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public EarthElementalEnergyBallEntity(LivingEntity owner, ItemStack staffStack, int power) {
        super(EWEntities.EARTH_ELEMENTAL_ENERGY_BALL, owner, staffStack, power);
    }

    @Override
    public Element getElement() {
        return EWElements.EARTH;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        forEachLivingEntity(power * 2,
                entity -> 0.5f * power,
                LivingEntity::isAlive,
                entity -> BlockPos.stream(entity.getBoundingBox()).forEach(this::placeDirt),
                this::placeDirts);
    }

    private void placeDirt(BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.isAir() || state.isReplaceable() || !state.getFluidState().isEmpty()) {
            if (getOwner() instanceof LivingEntity living && living.getBoundingBox().intersects(new Box(pos))) {
                return;
            }
            world.setBlockState(pos, Blocks.DIRT.getDefaultState());
        }
    }

    private void placeDirts() {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    BlockPos pos = this.getBlockPos().add(i, j, k);
                    placeDirt(pos);
                }
            }
        }
    }
}
