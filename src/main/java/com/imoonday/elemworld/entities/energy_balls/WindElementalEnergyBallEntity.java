package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WindElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {

    public WindElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public WindElementalEnergyBallEntity(LivingEntity owner, ItemStack staffStack) {
        super(EWEntities.WIND_ELEMENTAL_ENERGY_BALL, owner, staffStack);
    }

    @Override
    public Element getElement() {
        return EWElements.WIND;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        forEachLivingEntity(3.0f, this::blowUpEntities, true);
        blowUpBlocks();
    }

    private void blowUpBlocks() {
        if (world.isClient) {
            return;
        }
        BlockPos.stream(this.getBoundingBox().expand(5)).filter(pos -> {
            BlockState state = world.getBlockState(pos);
            if (state.isAir()) {
                return false;
            }
            if (state.getBlock().getHardness() == -1) {
                return false;
            }
            if (world.getBlockEntity(pos) != null) {
                return false;
            }
            int count = 0;
            BlockPos blockPos = pos;
            while (count < 8) {
                blockPos = blockPos.up();
                if (world.getBlockState(blockPos).isAir()) {
                    count++;
                    continue;
                }
                return false;
            }
            return true;
        }).forEach(pos -> {
            BlockState state = world.getBlockState(pos);
            FallingBlockEntity fallingBlock = FallingBlockEntity.spawnFromBlock(world, pos, state);
            fallingBlock.setHurtEntities(2.0f, 4);
            fallingBlock.setVelocity(random.nextDouble() * 2 - 1, random.nextDouble() + 1, random.nextDouble() * 2 - 1);
        });
    }

    private void blowUpEntities(LivingEntity entity) {
        entity.addVelocity(random.nextDouble() * 2 - 1, random.nextDouble() + 1, random.nextDouble() * 2 - 1);
    }
}
