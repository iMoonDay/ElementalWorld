package com.imoonday.elemworld.init;

import com.imoonday.elemworld.items.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;
import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWItems {

    public static final HashMap<Item, Model> MODELS = new HashMap<>();

    public static final Item ELEMENT_DETECTOR = register("element_detector", new ElementDetectorItem(), Models.HANDHELD);
    public static final Item ELEMENT_STICK = register("element_stick");
    public static final Item BASE_ELEMENT_FRAGMENT = register("base_element_fragment", new ElementFragmentItem(1));
    public static final Item ADVANCED_ELEMENT_FRAGMENT = register("advanced_element_fragment", new ElementFragmentItem(2));
    public static final Item RARE_ELEMENT_FRAGMENT = register("rare_element_fragment", new ElementFragmentItem(3));
    public static final Item ELEMENT_INGOT = register("element_ingot");
    public static final Item ELEMENT_BOOK = registerItem("element_book", new ElementBookItem());

    public static final Item ELEMENT_SWORD = register("element_sword", new ElementSwordItem(3, -2.4f), Models.HANDHELD);
    public static final Item ELEMENT_PICKAXE = register("element_pickaxe", ElementToolItem.createPickaxe(1, -2.8f), Models.HANDHELD);
    public static final Item ELEMENT_AXE = register("element_axe", ElementToolItem.createAxe(5.5f, -3.0f), Models.HANDHELD);
    public static final Item ELEMENT_SHOVEL = register("element_shovel", ElementToolItem.createShovel(1.5f, -3.0f), Models.HANDHELD);
    public static final Item ELEMENT_HOE = register("element_hoe", ElementToolItem.createHoe(-2, -0.5f), Models.HANDHELD);
    public static final Item ELEMENT_BOW = register("element_bow", new ElementBowItem(), null);

    public static void register() {
        LOGGER.info("Loading Items");
    }

    public static <T extends Item> T register(String id, T item, @Nullable Model model) {
        ItemGroupEvents.modifyEntriesEvent(EWItemGroups.ELEMENTAL_WORLD).register(content -> content.add(item));
        return registerItem(id, item, model);
    }

    public static <T extends Item> T register(String id, T item) {
        return register(id, item, Models.GENERATED);
    }

    public static <T extends Item> T registerItem(String id, T item, @Nullable Model model) {
        if (model != null) {
            MODELS.put(item, model);
        }
        return Registry.register(Registries.ITEM, id(id), item);
    }

    public static <T extends Item> T registerItem(String id, T item) {
        return registerItem(id, item, Models.GENERATED);
    }

    private static Item register(String id, @Nullable Model model) {
        return register(id, new Item(new FabricItemSettings()), model);
    }

    private static Item register(String id) {
        return register(id, Models.GENERATED);
    }

    private static Item register(String id, int count, @Nullable Model model) {
        return register(id, new Item(new FabricItemSettings().maxCount(count)), model);
    }
}
