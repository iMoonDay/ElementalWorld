package com.imoonday.elemworld.api;

import com.imoonday.elemworld.init.EWItemGroups;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

import static com.imoonday.elemworld.init.EWIdentifiers.id;

public interface EWRegister {

    ItemGroup GROUP = EWItemGroups.ELEMENTAL_WORLD;
    HashMap<Item, Model> ITEM_MODELS = new HashMap<>();
    HashMap<Block, Consumer<BlockLootTableGenerator>> BLOCK_DROPS = new HashMap<>();
    int DEFAULT_DURATION = 60 * 20;

    static <T extends Item> T register(String id, T item, @Nullable Model model) {
        ItemGroupEvents.modifyEntriesEvent(GROUP).register(content -> content.add(item));
        return registerItem(id, item, model);
    }

    static <T extends Item> T register(String id, T item) {
        return register(id, item, Models.GENERATED);
    }

    static <T extends Item> T registerItem(String id, T item, @Nullable Model model) {
        if (model != null) {
            ITEM_MODELS.put(item, model);
        }
        return Registry.register(Registries.ITEM, id(id), item);
    }

    static <T extends Item> T registerItem(String id, T item) {
        return registerItem(id, item, Models.GENERATED);
    }

    static Item register(String id, @Nullable Model model) {
        return register(id, new Item(new FabricItemSettings()), model);
    }

    static Item register(String id) {
        return register(id, Models.GENERATED);
    }

    static Item register(String id, int count, @Nullable Model model) {
        return register(id, new Item(new FabricItemSettings().maxCount(count)), model);
    }

    static Block register(String id, Block block) {
        register(id, (Item) new BlockItem(block, new Item.Settings()));
        return registerBlock(id, block, generator -> generator.addDrop(block));
    }

    static Block register(String id, Block block, ItemConvertible... drops) {
        register(id, (Item) new BlockItem(block, new Item.Settings()));
        return registerBlock(id, block, generator -> Arrays.stream(drops).toList().forEach(itemConvertible -> generator.addDrop(block, itemConvertible)));
    }

    static Block registerBlock(String id, Block block, Consumer<BlockLootTableGenerator> addDrop) {
        BLOCK_DROPS.put(block, addDrop);
        return Registry.register(Registries.BLOCK, id(id), block);
    }

    static StatusEffect registerEffect(String id, StatusEffect effect) {
        return Registry.register(Registries.STATUS_EFFECT, id(id), effect);
    }

    static Potion registerPotion(String id, StatusEffect effect, @NotNull Potion input, @NotNull Item item) {
        Potion potion = registerPotion(id, effect);
        BrewingRecipeRegistry.registerPotionRecipe(input, item, potion);
        return potion;
    }

    static Potion registerPotion(String id, StatusEffect effect) {
        Potion potion = new Potion(new StatusEffectInstance(effect, DEFAULT_DURATION));
        return Registry.register(Registries.POTION, id(id), potion);
    }

    static void register(EWRegister register){}
}
