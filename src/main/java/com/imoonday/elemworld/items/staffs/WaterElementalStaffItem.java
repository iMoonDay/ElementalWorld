package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.WaterElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWEffects;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.items.AbstractElementalStaffItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WaterElementalStaffItem extends AbstractElementalStaffItem {
    @Override
    public Element getElement() {
        return EWElements.WATER;
    }

    @Override
    public AbstractElementalEnergyBallEntity getEnergyBallEntity(LivingEntity user, ItemStack stack, int useTicks) {
        return new WaterElementalEnergyBallEntity(user, stack, getPower(useTicks));
    }

    @Override
    protected void onUsing(ItemStack stack, World world, LivingEntity user, int useTicks) {
        int power = getPower(useTicks);
        forEachLivingEntity(world, user, power * 2, entity -> {
            BlockPos.stream(entity.getBoundingBox()).forEach(pos -> {
                BlockState state = world.getBlockState(pos);
                if (state.isAir() || state.isReplaceable()) {
                    world.setBlockState(pos, Blocks.WATER.getDefaultState());
                }
            });
            entity.addStatusEffect(new StatusEffectInstance(EWEffects.WATER_FEARING, power * 2 * 20, power - 1));
        }, () -> {
        });
    }

    @Override
    protected SoundEvent getSoundEvent() {
        return SoundEvents.BLOCK_WATER_AMBIENT;
    }
}
