package com.imoonday.elemworld.init;

import com.imoonday.elemworld.items.*;
import com.imoonday.elemworld.items.staffs.FireElementalStaffItem;
import com.imoonday.elemworld.items.staffs.IceElementalStaffItem;
import com.imoonday.elemworld.items.staffs.WindElementalStaffItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;
import static com.imoonday.elemworld.ElementalWorldData.addTranslation;
import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWItems {

    public static final HashMap<Item, Model> ITEM_MODELS = new HashMap<>();
    public static final HashMap<Item, List<TagKey<Item>>> ITEM_TAGS = new HashMap<>();

    public static final Item ELEMENT_DETECTOR = register("element_detector", new ElementDetectorItem(), Models.HANDHELD, "Element Detector", "元素探测器");
    public static final Item ELEMENT_STICK = register("element_stick", "Element Stick", "元素棍");
    public static final Item BASE_ELEMENT_FRAGMENT = register("base_element_fragment", new ElementFragmentItem(1), "Base Element Fragment", "基础元素碎块");
    public static final Item ADVANCED_ELEMENT_FRAGMENT = register("advanced_element_fragment", new ElementFragmentItem(2), "Advanced Element Fragment", "进阶元素碎块");
    public static final Item RARE_ELEMENT_FRAGMENT = register("rare_element_fragment", new ElementFragmentItem(3), "Rare Element Fragment", "稀有元素碎块");
    public static final Item ELEMENT_INGOT = register("element_ingot", "Element Ingot", "元素锭");
    public static final Item ELEMENT_BOOK = registerItem("element_book", new ElementBookItem(), "Element Book", "元素书");
    public static final Item ELEMENT_SWORD = register("element_sword", new ElementSwordItem(3, -2.4f), Models.HANDHELD, "Element Sword", "元素剑", ItemTags.SWORDS, EWTags.ELEMENT_TOOLS_AND_WEAPONS);
    public static final Item ELEMENT_PICKAXE = register("element_pickaxe", ElementTools.createPickaxe(1, -2.8f), Models.HANDHELD, "Element Pickaxe", "元素镐", ItemTags.PICKAXES, EWTags.ELEMENT_TOOLS_AND_WEAPONS);
    public static final Item ELEMENT_AXE = register("element_axe", ElementTools.createAxe(5.5f, -3.0f), Models.HANDHELD, "Element Axe", "元素斧", ItemTags.AXES, EWTags.ELEMENT_TOOLS_AND_WEAPONS);
    public static final Item ELEMENT_SHOVEL = register("element_shovel", ElementTools.createShovel(1.5f, -3.0f), Models.HANDHELD, "Element Shovel", "元素铲", ItemTags.SHOVELS, EWTags.ELEMENT_TOOLS_AND_WEAPONS);
    public static final Item ELEMENT_HOE = register("element_hoe", ElementTools.createHoe(-2, -0.5f), Models.HANDHELD, "Element Hoe", "元素锄", ItemTags.HOES, EWTags.ELEMENT_TOOLS_AND_WEAPONS);
    public static final Item ELEMENT_BOW = register("element_bow", new ElementBowItem(), null, "Element Bow", "元素弓");
    public static final Item WIND_ELEMENTAL_STAFF = register("wind_elemental_staff", new WindElementalStaffItem(), Models.HANDHELD, "Wind Elemental Staff", "风元素法杖");
    public static final Item FIRE_ELEMENTAL_STAFF = register("fire_elemental_staff", new FireElementalStaffItem(), Models.HANDHELD, "Fire Elemental Staff", "火元素法杖");
    public static final Item ICE_ELEMENTAL_STAFF = register("ice_elemental_staff", new IceElementalStaffItem(), Models.HANDHELD, "Ice Elemental Staff", "冰元素法杖");

    public static void register() {
        LOGGER.info("Loading Items");
    }

    @SafeVarargs
    public static <T extends Item> T register(String id, T item, @Nullable Model model, String en_us, @Nullable String zh_cn, TagKey<Item>... tags) {
        ItemGroupEvents.modifyEntriesEvent(EWItemGroups.ELEMENTAL_WORLD).register(content -> content.add(item));
        return registerItem(id, item, model, en_us, zh_cn, tags);
    }

    @SafeVarargs
    public static <T extends Item> T register(String id, T item, String en_us, @Nullable String zh_cn, TagKey<Item>... tags) {
        return register(id, item, Models.GENERATED, en_us, zh_cn, tags);
    }

    @SafeVarargs
    public static <T extends Item> T registerItem(String id, T item, @Nullable Model model, String en_us, @Nullable String zh_cn, TagKey<Item>... tags) {
        if (model != null) {
            ITEM_MODELS.put(item, model);
        }
        if (tags != null) {
            List<TagKey<Item>> list = Arrays.asList(tags);
            list.removeIf(Objects::isNull);
            if (list.size() > 0) {
                ITEM_TAGS.put(item, list);
            }
        }
        addTranslation(item, en_us, zh_cn);
        return Registry.register(Registries.ITEM, id(id), item);
    }

    @SafeVarargs
    public static <T extends Item> T registerItem(String id, T item, String en_us, @Nullable String zh_cn, TagKey<Item>... tags) {
        return registerItem(id, item, Models.GENERATED, en_us, zh_cn, tags);
    }

    @SafeVarargs
    public static Item register(String id, @Nullable Model model, String en_us, @Nullable String zh_cn, TagKey<Item>... tags) {
        return register(id, new Item(new FabricItemSettings()), model, en_us, zh_cn, tags);
    }

    @SafeVarargs
    public static Item register(String id, String en_us, @Nullable String zh_cn, TagKey<Item>... tags) {
        return register(id, Models.GENERATED, en_us, zh_cn, tags);
    }

    @SafeVarargs
    public static Item register(String id, int count, @Nullable Model model, String en_us, @Nullable String zh_cn, TagKey<Item>... tags) {
        return register(id, new Item(new FabricItemSettings().maxCount(count)), model, en_us, zh_cn, tags);
    }
}
