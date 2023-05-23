package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.WindElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.items.AbstractElementalStaffItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class WindElementalStaffItem extends AbstractElementalStaffItem {

    @Override
    protected void onUsing(ItemStack stack, World world, LivingEntity user, int useTicks) {
        int power = getPower(useTicks);
        forEachLivingEntity(world, user, power * 2, entity -> entity.setVelocity(0, 0.5 * power, 0), () -> {});
    }

    @Override
    public Element getElement() {
        return EWElements.WIND;
    }

    @Override
    public AbstractElementalEnergyBallEntity getEnergyBallEntity(LivingEntity user, ItemStack stack, int useTicks) {
        return new WindElementalEnergyBallEntity(user, stack, getPower(useTicks));
    }

    @Override
    protected SoundEvent getSoundEvent() {
        return SoundEvents.ENTITY_ALLAY_ITEM_GIVEN;
    }
}
