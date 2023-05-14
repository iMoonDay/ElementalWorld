package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.IceElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWEffects;
import com.imoonday.elemworld.init.EWElements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class IceElementalStaffItem extends AbstractElementalStaffItem {
    public IceElementalStaffItem() {
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
            living.addStatusEffect(new StatusEffectInstance(EWEffects.FREEZE, power * 20 + 1));
            getElement().addEffect(living, user);
        }
    }

    @Override
    public Element getElement() {
        return EWElements.ICE;
    }

    @Override
    public int getPower(int useTicks) {
        return Math.min(useTicks / 10, 5);
    }

    @Override
    public AbstractElementalEnergyBallEntity getEnergyBallEntity(LivingEntity user, int useTicks) {
        return new IceElementalEnergyBallEntity(user, getPower(useTicks));
    }

    @Override
    protected SoundEvent getSoundEvent() {
        return SoundEvents.BLOCK_GLASS_BREAK;
    }
}
