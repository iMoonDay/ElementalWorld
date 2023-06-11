package com.imoonday.elemworld.items.staffs;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.entities.energy_balls.ThunderElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.items.AbstractElementalStaffItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Map;
import java.util.function.Predicate;

public class ThunderElementalStaffItem extends AbstractElementalStaffItem {

    public ThunderElementalStaffItem() {
        super(64);
    }

    @Override
    public Element getBaseElement() {
        return EWElements.THUNDER;
    }

    @Override
    public AbstractElementalEnergyBallEntity createEnergyBall(LivingEntity user, ItemStack stack) {
        return new ThunderElementalEnergyBallEntity(user, stack);
    }

    @Override
    protected void addEffects(ItemStack stack, World world, LivingEntity user) {
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 30 * 20, 1));
    }

    @Override
    protected Map<Predicate<LivingEntity>, ItemStack[]> addLootables(Map<Predicate<LivingEntity>, ItemStack[]> lootables) {
        lootables.put(entity -> entity instanceof CreeperEntity creeper && creeper.shouldRenderOverlay() && creeper.getRandom().nextFloat() < 0.0125f, new ItemStack[]{new ItemStack(this)});
        return lootables;
    }
}
