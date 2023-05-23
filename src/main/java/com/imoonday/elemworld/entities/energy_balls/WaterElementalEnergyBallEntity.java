package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWEffects;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WaterElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {
    public WaterElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public WaterElementalEnergyBallEntity(LivingEntity owner, ItemStack staffStack, int power) {
        super(EWEntities.WATER_ELEMENTAL_ENERGY_BALL, owner, staffStack, power);
    }

    @Override
    public Element getElement() {
        return EWElements.WATER;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        forEachLivingEntity(power * 2,
                entity -> 0.5f * power,
                LivingEntity::isAlive,
                this::setInWater,
                this::spawnWater);
    }

    private void setInWater(LivingEntity entity) {
        BlockPos.stream(entity.getBoundingBox()).forEach(pos -> {
            BlockState state = world.getBlockState(pos);
            if (state.isAir() || state.isReplaceable()) {
                world.setBlockState(pos, Blocks.WATER.getDefaultState());
            }
        });
        entity.addStatusEffect(new StatusEffectInstance(EWEffects.WATER_FEARING, power * 2 * 20, power - 1));
    }

    private void spawnWater() {
        BlockPos.stream(this.getBoundingBox().expand(power - 1))
                .filter(pos -> world.getBlockState(pos).isAir() || world.getBlockState(pos).isReplaceable())
                .forEach(pos -> world.setBlockState(pos, Blocks.WATER.getDefaultState()));
    }
}
