package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public abstract class AbstractElementalStaffItem extends Item {

    public AbstractElementalStaffItem(int maxDamage) {
        super(new FabricItemSettings().maxCount(1).maxDamage(maxDamage));
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int useTicks = getMaxUseTime(stack) - remainingUseTicks;
        if (useTicks < getMinUseTime()) {
            return;
        }
        int amount;
        SoundEvent soundEvent;
        boolean success = true;
        AbstractElementalEnergyBallEntity energyBallEntity = getEnergyBallEntity(user, useTicks);
        if (user.isSneaking() || energyBallEntity == null) {
            onUsing(stack, world, user, useTicks);
            amount = 1;
            soundEvent = getSoundEvent();
        } else {
            success = world.spawnEntity(energyBallEntity);
            amount = 2;
            soundEvent = SoundEvents.ENTITY_SNOWBALL_THROW;
        }
        if (success) {
            stack.damage(amount, user, p -> p.sendToolBreakStatus(user.getActiveHand()));
            world.playSound(null, user.getBlockPos(), soundEvent, SoundCategory.VOICE);
        }
    }

    public abstract Element getElement();

    public abstract int getPower(int useTicks);

    public abstract AbstractElementalEnergyBallEntity getEnergyBallEntity(LivingEntity user, int useTicks);

    protected abstract int getMinUseTime();

    protected abstract void onUsing(ItemStack stack, World world, LivingEntity user, int useTicks);

    protected abstract SoundEvent getSoundEvent();

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) {
            return TypedActionResult.fail(itemStack);
        }
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }
}
