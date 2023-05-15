package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.FireElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.items.AbstractElementalStaffItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class FireElementalStaffItem extends AbstractElementalStaffItem {
    public FireElementalStaffItem() {
        super(128);
    }

    @Override
    protected void onUsing(ItemStack stack, World world, LivingEntity user, int useTicks) {
        int power = getPower(useTicks);
        forEachLivingEntity(world, user, power * 2, entity -> entity.setOnFireFor(power * 2));
    }

    @Override
    public Element getElement() {
        return EWElements.FIRE;
    }

    @Override
    public AbstractElementalEnergyBallEntity getEnergyBallEntity(LivingEntity user, ItemStack stack, int useTicks) {
        return new FireElementalEnergyBallEntity(user, stack, getPower(useTicks));
    }

    @Override
    protected SoundEvent getSoundEvent() {
        return SoundEvents.ITEM_FIRECHARGE_USE;
    }
}
