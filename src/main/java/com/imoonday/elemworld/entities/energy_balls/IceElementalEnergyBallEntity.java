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
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IceElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {

    public IceElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public IceElementalEnergyBallEntity(LivingEntity owner, ItemStack staffStack) {
        super(EWEntities.ICE_ELEMENTAL_ENERGY_BALL, owner, staffStack);
    }

    @Override
    public Element getElement() {
        return EWElements.ICE;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        forEachLivingEntity(3.0f, this::addStatusEffects, true);
    }

    @Override
    protected boolean shouldCollide(HitResult hitResult) {
        return !(hitResult instanceof BlockHitResult blockHitResult) || !world.getBlockState(blockHitResult.getBlockPos()).isOf(Blocks.FROSTED_ICE);
    }

    @Override
    public boolean collidesWithStateAtPos(BlockPos pos, BlockState state) {
        return super.collidesWithStateAtPos(pos, state);
    }

    private void addStatusEffects(LivingEntity living) {
        living.addStatusEffect(new StatusEffectInstance(EWEffects.FREEZE, 15 * 20));
    }

    @Override
    protected SoundEvent getSoundEvent() {
        return SoundEvents.BLOCK_GLASS_BREAK;
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos.stream(this.getBoundingBox().expand(1))
                .filter(pos -> world.getBlockState(pos).isOf(Blocks.WATER))
                .forEach(pos -> world.setBlockState(pos, Blocks.FROSTED_ICE.getDefaultState(), Block.NOTIFY_LISTENERS));
    }
}
