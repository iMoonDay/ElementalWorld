package com.imoonday.elemworld.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.SwordItem;

public class ElementSwordItem extends SwordItem {
    public ElementSwordItem(int attackDamage, float attackSpeed) {
        super(ElementTools.ELEMENT_MATERIAL, attackDamage, attackSpeed, new FabricItemSettings());
    }
}
