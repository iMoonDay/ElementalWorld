package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.IceElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWEffects;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.items.AbstractElementalStaffItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class IceElementalStaffItem extends AbstractElementalStaffItem {

    @Override
    protected void onUsing(ItemStack stack, World world, LivingEntity user, int useTicks) {
        int power = getPower(useTicks);
        forEachLivingEntity(world, user, power * 2, entity -> entity.addStatusEffect(new StatusEffectInstance(EWEffects.FREEZE, power * 20 + 1)), () -> {});
    }

    @Override
    public Element getElement() {
        return EWElements.ICE;
    }

    @Override
    public AbstractElementalEnergyBallEntity getEnergyBallEntity(LivingEntity user, ItemStack stack, int useTicks) {
        return new IceElementalEnergyBallEntity(user, stack, getPower(useTicks));
    }

    @Override
    protected SoundEvent getSoundEvent() {
        return SoundEvents.BLOCK_GLASS_BREAK;
    }
}
