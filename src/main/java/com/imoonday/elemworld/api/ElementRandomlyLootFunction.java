package com.imoonday.elemworld.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.imoonday.elemworld.items.ElementBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.math.random.Random;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ElementRandomlyLootFunction extends ConditionalLootFunction {

    final List<Element> elements;

    ElementRandomlyLootFunction(LootCondition[] conditions, Collection<Element> elements) {
        super(conditions);
        this.elements = ImmutableList.copyOf(elements);
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.SET_NBT;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        boolean success = ElementBookItem.addElement(stack, ElementEntry.createRandom(element -> !element.isInvalid()));
        Random random = Random.create();
        float chance = 0.25f;
        while (random.nextFloat() < chance && success) {
            success = ElementBookItem.addElement(stack, ElementEntry.createRandom(element -> !element.isInvalid()));
            chance *= 0.5f;
        }
        return stack;
    }

    public static ElementRandomlyLootFunction.Builder create() {
        return new ElementRandomlyLootFunction.Builder();
    }

    public static ConditionalLootFunction.Builder<?> builder() {
        return ElementRandomlyLootFunction.builder(conditions -> new ElementRandomlyLootFunction(conditions, ImmutableList.of()));
    }

    public static class Builder
            extends ConditionalLootFunction.Builder<ElementRandomlyLootFunction.Builder> {
        private final Set<Element> elements = Sets.newHashSet();

        @Override
        protected ElementRandomlyLootFunction.Builder getThisBuilder() {
            return this;
        }

        public ElementRandomlyLootFunction.Builder add(Element element) {
            this.elements.add(element);
            return this;
        }

        @Override
        public LootFunction build() {
            return new ElementRandomlyLootFunction(this.getConditions(), this.elements);
        }
    }
}
