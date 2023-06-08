package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWEffects;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class WaterElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {

    public WaterElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public WaterElementalEnergyBallEntity(LivingEntity owner, ItemStack staffStack) {
        super(EWEntities.WATER_ELEMENTAL_ENERGY_BALL, owner, staffStack);
    }

    @Override
    public Element getElement() {
        return EWElements.WATER;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        forEachLivingEntity(3.0f, this::addEffects, true);
        suppressFire();
    }

    @Override
    protected SoundEvent getSoundEvent() {
        return SoundEvents.ITEM_BUCKET_EMPTY;
    }

    private void addEffects(LivingEntity entity) {
        entity.addStatusEffect(new StatusEffectInstance(EWEffects.WATER_FEARING, 15 * 20));
        entity.setAir(0);
        BlockPos.stream(entity.getBoundingBox())
                .filter(blockPos -> world.getBlockState(blockPos).isAir() || world.getBlockState(blockPos).isReplaceable())
                .forEach(blockPos -> world.setBlockState(blockPos, Blocks.WATER.getDefaultState(), Block.NOTIFY_LISTENERS));
    }

    private void suppressFire() {
        if (world.isClient) {
            return;
        }
        replaceBlocks(blockState -> blockState.isOf(Blocks.LAVA) && !blockState.getFluidState().isStill(), Blocks.STONE, true);
        replaceBlocks(blockState -> blockState.getFluidState().isEqualAndStill(Fluids.LAVA), Blocks.OBSIDIAN, true);
        replaceBlocks(blockState -> blockState.isOf(Blocks.WATER) && !blockState.getFluidState().isStill(), Blocks.WATER, false);
        replaceBlocks(Blocks.FIRE, Blocks.WATER, true);
        replaceBlocks(Blocks.PACKED_ICE, Blocks.BLUE_ICE, false);
        replaceBlocks(Blocks.ICE, Blocks.PACKED_ICE, false);
        replaceBlocks(Blocks.FROSTED_ICE, Blocks.ICE, false);
        replaceBlocks(Blocks.SNOW_BLOCK, Blocks.FROSTED_ICE, false);
        replaceBlocks(Blocks.SNOW, Blocks.SNOW_BLOCK, false);
    }

    private void replaceBlocks(Predicate<BlockState> predicate, Block block, boolean playSound) {
        BlockPos.stream(this.getBoundingBox().expand(15))
                .filter(blockPos -> predicate.test(world.getBlockState(blockPos)))
                .forEach(blockPos -> {
                    world.setBlockState(blockPos, block.getDefaultState(), Block.NOTIFY_LISTENERS);
                    if (playSound) {
                        world.playSound(null, blockPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS);
                    }
                });
    }

    private void replaceBlocks(Block filter, Block block, boolean playSound) {
        replaceBlocks(blockState -> blockState.isOf(filter), block, playSound);
    }
}
