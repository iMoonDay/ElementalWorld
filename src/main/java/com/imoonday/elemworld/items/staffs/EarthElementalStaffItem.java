package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.items.AbstractElementalStaffItem;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class EarthElementalStaffItem extends AbstractElementalStaffItem {
    public EarthElementalStaffItem() {
        super(128);
    }

    @Override
    public Element getElement() {
        return EWElements.EARTH;
    }

    @Override
    public AbstractElementalEnergyBallEntity getEnergyBallEntity(LivingEntity user, ItemStack stack, int useTicks) {
        return null;
    }

    @Override
    protected void onUsing(ItemStack stack, World world, LivingEntity user, int useTicks) {
        int power = getPower(useTicks);
        forEachLivingEntity(world, user, power * 2, entity -> {
            Set<BlockPos> posSet = new HashSet<>();
            BlockPos blockPos = entity.getBlockPos();
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        posSet.add(blockPos.add(i, j, k));
//                        FallingBlockEntity.spawnFromBlock(world, pos, Blocks.DIRT.getDefaultState());
                    }
                }
            }
            for (BlockPos pos : posSet) {
                world.setBlockState(pos, Blocks.DIRT.getDefaultState());
            }
        });
    }

    @Override
    protected SoundEvent getSoundEvent() {
        return SoundEvents.BLOCK_ROOTED_DIRT_FALL;
    }
}
