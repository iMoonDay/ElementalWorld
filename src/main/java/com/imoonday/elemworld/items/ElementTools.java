package com.imoonday.elemworld.items;

import com.imoonday.elemworld.init.EWItems;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;

public class ElementTools {

    public static final ElementToolMaterial ELEMENT_MATERIAL = new ElementToolMaterial();

    public static ElementPickaxeItem createPickaxe(int attackDamage, float attackSpeed) {
        return new ElementPickaxeItem(attackDamage, attackSpeed);
    }

    public static ElementAxeItem createAxe(float attackDamage, float attackSpeed) {
        return new ElementAxeItem(attackDamage, attackSpeed);
    }

    public static ElementShovelItem createShovel(float attackDamage, float attackSpeed) {
        return new ElementShovelItem(attackDamage, attackSpeed);
    }

    public static ElementHoeItem createHoe(int attackDamage, float attackSpeed) {
        return new ElementHoeItem(attackDamage, attackSpeed);
    }

    public static class ElementPickaxeItem extends PickaxeItem {

        private ElementPickaxeItem(int attackDamage, float attackSpeed) {
            super(ElementTools.ELEMENT_MATERIAL, attackDamage, attackSpeed, new FabricItemSettings());
        }
    }

    public static class ElementAxeItem extends AxeItem {

        private ElementAxeItem(float attackDamage, float attackSpeed) {
            super(ElementTools.ELEMENT_MATERIAL, attackDamage, attackSpeed, new FabricItemSettings());
        }
    }

    public static class ElementShovelItem extends ShovelItem {

        private ElementShovelItem(float attackDamage, float attackSpeed) {
            super(ElementTools.ELEMENT_MATERIAL, attackDamage, attackSpeed, new FabricItemSettings());
        }
    }

    public static class ElementHoeItem extends HoeItem {

        private ElementHoeItem(int attackDamage, float attackSpeed) {
            super(ElementTools.ELEMENT_MATERIAL, attackDamage, attackSpeed, new FabricItemSettings());
        }
    }

    private static class ElementToolMaterial implements ToolMaterial {

        @Override
        public int getDurability() {
            return 655;
        }

        @Override
        public float getMiningSpeedMultiplier() {
            return 10.0f;
        }

        @Override
        public float getAttackDamage() {
            return 2.5f;
        }

        @Override
        public int getMiningLevel() {
            return MiningLevels.DIAMOND;
        }

        @Override
        public int getEnchantability() {
            return 16;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.ofItems(EWItems.ELEMENT_INGOT);
        }
    }
}
