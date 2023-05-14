package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.WindElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class WindElementalStaffItem extends AbstractElementalStaffItem {

    public WindElementalStaffItem() {
        super(128);
    }

    @Override
    protected int getMinUseTime() {
        return 10;
    }

    @Override
    protected void onUsing(ItemStack stack, World world, LivingEntity user, int useTicks) {
        int power = getPower(useTicks);
        for (Entity entity : world.getOtherEntities(user, user.getBoundingBox().expand(power * 2), entity -> entity instanceof LivingEntity living && living.isAlive())) {
            LivingEntity living = (LivingEntity) entity;
            living.setVelocity(0, 0.5 * power, 0);
            getElement().addEffect(living, user);
        }
    }

    @Override
    public Element getElement() {
        return EWElements.WIND;
    }

    @Override
    public AbstractElementalEnergyBallEntity getEnergyBallEntity(LivingEntity user, int useTicks) {
        return new WindElementalEnergyBallEntity(user, getPower(useTicks));
    }

    @Override
    public int getPower(int useTicks) {
        return Math.min(useTicks / 10, 5);
    }

    @Override
    protected SoundEvent getSoundEvent() {
        return SoundEvents.ENTITY_ALLAY_ITEM_GIVEN;
    }
}
