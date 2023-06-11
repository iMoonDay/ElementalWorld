package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.block.Fertilizable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WoodElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {

    public WoodElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public WoodElementalEnergyBallEntity(LivingEntity owner, ItemStack staffStack) {
        super(EWEntities.WOOD_ELEMENTAL_ENERGY_BALL, owner, staffStack);
    }

    @Override
    public Element getElement() {
        return EWElements.WOOD;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        forEachLivingEntity(2.0f, this::poisonAndLoot, false);
        growAndBreakWoods();
    }

    private void poisonAndLoot(LivingEntity entity) {
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 10 * 20));
        ItemStack mainhandStack = entity.getMainHandStack().copy();
        if (!mainhandStack.isEmpty()) {
            if (random.nextFloat() < 0.075f) {
                entity.dropStack(mainhandStack);
                entity.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            }
        } else {
            ItemStack offhandStack = entity.getOffHandStack().copy();
            if (!offhandStack.isEmpty()) {
                if (random.nextFloat() < 0.075f) {
                    entity.dropStack(offhandStack);
                    entity.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
                }
            }
        }
    }

    private void growAndBreakWoods() {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }
        BlockPos.stream(this.getBoundingBox().expand(10))
                .filter(pos -> world.getBlockState(pos).isIn(BlockTags.LOGS) || world.getBlockState(pos).isIn(BlockTags.LEAVES))
                .forEach(pos -> world.breakBlock(pos, true, this.getOwner()));

        BlockPos.stream(this.getBoundingBox().expand(10))
                .filter(pos -> world.getBlockState(pos).isIn(BlockTags.SAPLINGS) && world.getBlockState(pos).getBlock() instanceof Fertilizable fertilizable && fertilizable.canGrow(world, random, pos, world.getBlockState(pos)))
                .forEach(pos -> ((Fertilizable) world.getBlockState(pos).getBlock()).grow(serverWorld, random, pos, world.getBlockState(pos)));
    }
}
