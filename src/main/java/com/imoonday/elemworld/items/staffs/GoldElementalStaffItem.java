package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.GoldElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.items.AbstractElementalStaffItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GoldElementalStaffItem extends AbstractElementalStaffItem {

    @Override
    protected void onUsing(ItemStack stack, World world, LivingEntity user, int useTicks) {
        int power = getPower(useTicks);
        forEachLivingEntity(world, user, power * 2, entity -> knockBack(user, power, entity), () -> {});
    }

    private static void knockBack(LivingEntity user, int power, LivingEntity entity) {
        Vec3d vec3d = entity.getPos().subtract(user.getPos()).normalize().multiply(power);
        entity.setVelocity(vec3d.x, 0.5 + 0.1 * power, vec3d.z);
    }

    @Override
    public Element getElement() {
        return EWElements.GOLD;
    }

    @Override
    public AbstractElementalEnergyBallEntity getEnergyBallEntity(LivingEntity user, ItemStack stack, int useTicks) {
        return new GoldElementalEnergyBallEntity(user, stack, getPower(useTicks));
    }

    @Override
    protected SoundEvent getSoundEvent() {
        return SoundEvents.BLOCK_ANVIL_PLACE;
    }
}
