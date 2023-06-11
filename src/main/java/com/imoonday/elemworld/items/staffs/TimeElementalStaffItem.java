package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.TimeElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.items.AbstractElementalStaffItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Map;
import java.util.function.Predicate;

public class TimeElementalStaffItem extends AbstractElementalStaffItem {

    public TimeElementalStaffItem() {
        super(128);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.TIME;
    }

    @Override
    public AbstractElementalEnergyBallEntity createEnergyBall(LivingEntity user, ItemStack stack) {
        return new TimeElementalEnergyBallEntity(user, stack);
    }

    @Override
    protected void addEffects(ItemStack stack, World world, LivingEntity user) {
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 30 * 20, 1));
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 30 * 20));
    }

    @Override
    protected Map<Predicate<LivingEntity>, ItemStack[]> addLootables(Map<Predicate<LivingEntity>, ItemStack[]> lootables) {
        lootables.put(this::randomTime, new ItemStack[]{new ItemStack(this)});
        return lootables;
    }

    private boolean randomTime(LivingEntity entity) {
        World world = entity.world;
        long time = world.getTimeOfDay();
        float chance = (1000 - time % 1000) / 20000.0f;
        return world.random.nextFloat() < chance;
    }
}
