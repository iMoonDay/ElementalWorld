package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.WoodElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.items.AbstractElementalStaffItem;
import net.minecraft.block.Blocks;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class WoodElementalStaffItem extends AbstractElementalStaffItem {

    @Override
    public Element getElement() {
        return EWElements.WOOD;
    }

    @Override
    public AbstractElementalEnergyBallEntity getEnergyBallEntity(LivingEntity user, ItemStack stack, int useTicks) {
        return new WoodElementalEnergyBallEntity(user, stack, getPower(useTicks));
    }

    @Override
    protected void onUsing(ItemStack stack, World world, LivingEntity user, int useTicks) {
        int power = getPower(useTicks);
        forEachLivingEntity(world, user, power * 2, entity -> spawnFallingBlocks(world, power, entity), () -> spawnRandomFallingBlocks(world, power, user));
    }

    private static void spawnFallingBlocks(World world, int power, LivingEntity entity) {
        for (int i = 5; i <= 8; i++) {
            spawnFallingBlock(world, power, entity.getBlockPos().add(0, i, 0));
        }
    }

    private static void spawnRandomFallingBlocks(World world, int power, LivingEntity user) {
        int i = 1;
        while (i++ <= 3 * power) {
            Random random = user.getRandom();
            int x = random.nextBetween(-power * 2, power * 2);
            int z = random.nextBetween(-power * 2, power * 2);
            if (x != 0 || z != 0) {
                for (int j = 5; j <= 8; j++) {
                    spawnFallingBlock(world, power, user.getBlockPos().add(x, j, z));
                }
            }
        }
    }

    private static void spawnFallingBlock(World world, int power, BlockPos blockPos) {
        FallingBlockEntity fallingBlock = FallingBlockEntity.spawnFromBlock(world, blockPos, Blocks.OAK_LOG.getDefaultState());
        fallingBlock.setHurtEntities(0.5f * power, power);
        fallingBlock.setDestroyedOnLanding();
    }

    @Override
    protected SoundEvent getSoundEvent() {
        return SoundEvents.BLOCK_WOOD_PLACE;
    }
}
