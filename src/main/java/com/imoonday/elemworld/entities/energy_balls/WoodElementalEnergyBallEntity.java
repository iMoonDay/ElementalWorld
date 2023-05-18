package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.api.Element;
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

public class WoodElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {
    public WoodElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public WoodElementalEnergyBallEntity(LivingEntity owner, ItemStack staffStack, int power) {
        super(EWEntities.WOOD_ELEMENTAL_ENERGY_BALL, owner, staffStack, power);
    }

    @Override
    public Element getElement() {
        return EWElements.WOOD;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        forEachLivingEntity(power * 2,
                entity -> 0.5f * power,
                LivingEntity::isAlive,
                entity -> BlockPos.stream(entity.getBoundingBox()).forEach(this::placeWood),
                this::placeWoods);
    }

    private void placeWood(BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.isAir() || state.isReplaceable() || !state.getFluidState().isEmpty()) {
            if (getOwner() instanceof LivingEntity living && living.getBoundingBox().intersects(new Box(pos))) {
                return;
            }
            world.setBlockState(pos, Blocks.OAK_WOOD.getDefaultState());
        }
    }

    private void placeWoods() {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    BlockPos pos = this.getBlockPos().add(i, j, k);
                    placeWood(pos);
                }
            }
        }
    }
}
