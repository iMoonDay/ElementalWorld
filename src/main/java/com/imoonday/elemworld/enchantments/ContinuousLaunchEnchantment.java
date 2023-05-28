package com.imoonday.elemworld.enchantments;

import com.imoonday.elemworld.interfaces.IgnoreEnchantmentTarget;
import com.imoonday.elemworld.items.AbstractElementalStaffItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class ContinuousLaunchEnchantment extends Enchantment implements IgnoreEnchantmentTarget {
    public ContinuousLaunchEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof AbstractElementalStaffItem;
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        return super.canAccept(other);
    }
}
