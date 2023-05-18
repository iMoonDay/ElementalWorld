package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.EarthElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.items.AbstractElementalStaffItem;
import net.minecraft.block.Blocks;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EarthElementalStaffItem extends AbstractElementalStaffItem {

    @Override
    public Element getElement() {
        return EWElements.EARTH;
    }

    @Override
    public AbstractElementalEnergyBallEntity getEnergyBallEntity(LivingEntity user, ItemStack stack, int useTicks) {
        return new EarthElementalEnergyBallEntity(user, stack, getPower(useTicks));
    }

    @Override
    protected void onUsing(ItemStack stack, World world, LivingEntity user, int useTicks) {
        int power = getPower(useTicks);
        forEachLivingEntity(world, user, power * 2, entity -> BlockPos.stream(entity.getBoundingBox()).forEach(pos -> spawnFallingBlock(world, power, pos)), () -> {});
    }

    private static void spawnFallingBlock(World world, int power, BlockPos pos) {
        FallingBlockEntity fallingBlock = FallingBlockEntity.spawnFromBlock(world, pos.up(5), Blocks.DIRT.getDefaultState());
        fallingBlock.setHurtEntities(0.5f * power, power);
        fallingBlock.setDestroyedOnLanding();
    }

    @Override
    protected SoundEvent getSoundEvent() {
        return SoundEvents.BLOCK_ROOTED_DIRT_FALL;
    }
}
