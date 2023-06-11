package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.LightElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.items.AbstractElementalStaffItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Map;
import java.util.function.Predicate;

public class LightElementalStaffItem extends AbstractElementalStaffItem {

    public LightElementalStaffItem() {
        super(64);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.LIGHT;
    }

    @Override
    public AbstractElementalEnergyBallEntity createEnergyBall(LivingEntity user, ItemStack stack) {
        return new LightElementalEnergyBallEntity(user, stack);
    }

    @Override
    protected void addEffects(ItemStack stack, World world, LivingEntity user) {
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 30 * 20));
    }

    @Override
    protected Map<Predicate<LivingEntity>, ItemStack[]> addLootables(Map<Predicate<LivingEntity>, ItemStack[]> lootables) {
        lootables.put(entity -> entity.getRandom().nextFloat() < 0.00125f * (entity.world.isDay() ? 1.5f : 0.75f), new ItemStack[]{new ItemStack(this)});
        return lootables;
    }
}
