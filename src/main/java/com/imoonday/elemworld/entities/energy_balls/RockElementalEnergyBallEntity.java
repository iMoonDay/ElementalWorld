package com.imoonday.elemworld.entities.energy_balls;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RockElementalEnergyBallEntity extends AbstractElementalEnergyBallEntity {

    public RockElementalEnergyBallEntity(EntityType<? extends AbstractElementalEnergyBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public RockElementalEnergyBallEntity(LivingEntity owner, ItemStack staffStack) {
        super(EWEntities.ROCK_ELEMENTAL_ENERGY_BALL, owner, staffStack);
    }

    @Override
    public Element getElement() {
        return EWElements.ROCK;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        forEachLivingEntity(3.0f, this::addStatusEffects, true);
        spawnWall();
    }

    private void addStatusEffects(LivingEntity entity) {
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 3 * 20, 2));
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 15 * 20, 2));
    }

    private void spawnWall() {
        if (world.isClient) {
            return;
        }
        Block[] blocks = {Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.TUFF, Blocks.DEEPSLATE};
        int i = random.nextBetween(10, 20);
        BlockPos.stream(this.getBlockX() - i, this.getBlockY() + i, this.getBlockZ() - i, this.getBlockX() + i, this.getBlockY() + i, this.getBlockZ() + i).forEach(pos -> {
            if (world.getBlockState(pos).isAir() && pos.isWithinDistance(this.getBlockPos().up(i), i)) {
                BlockState state = blocks[random.nextInt(blocks.length)].getDefaultState();
                FallingBlockEntity fallingBlock = FallingBlockEntity.spawnFromBlock(world, pos, state);
                fallingBlock.setHurtEntities(3.0f, 10);
                fallingBlock.setDestroyedOnLanding();
            }
        });
    }
}
