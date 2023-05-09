package com.imoonday.elemworld.init;

import com.imoonday.elemworld.api.EWRegister;
import com.imoonday.elemworld.items.*;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;

public class EWItems {

    public static final Item ELEMENT_DETECTOR = EWRegister.register("element_detector", new ElementDetectorItem(), Models.HANDHELD);
    public static final Item ELEMENT_STICK = EWRegister.register("element_stick");
    public static final Item BASE_ELEMENT_FRAGMENT = EWRegister.register("base_element_fragment", new ElementFragmentItem(1));
    public static final Item ADVANCED_ELEMENT_FRAGMENT = EWRegister.register("advanced_element_fragment", new ElementFragmentItem(2));
    public static final Item RARE_ELEMENT_FRAGMENT = EWRegister.register("rare_element_fragment", new ElementFragmentItem(3));
    public static final Item ELEMENT_INGOT = EWRegister.register("element_ingot");
    public static final Item ELEMENT_BOOK = EWRegister.registerItem("element_book", new ElementBookItem());
    public static final Item ELEMENT_SWORD = EWRegister.register("element_sword", new ElementSwordItem(3, -2.4f), Models.HANDHELD);
    public static final Item ELEMENT_PICKAXE = EWRegister.register("element_pickaxe", ElementToolItem.createPickaxe(1, -2.8f), Models.HANDHELD);
    public static final Item ELEMENT_AXE = EWRegister.register("element_axe", ElementToolItem.createAxe(5.5f, -3.0f), Models.HANDHELD);
    public static final Item ELEMENT_SHOVEL = EWRegister.register("element_shovel", ElementToolItem.createShovel(1.5f, -3.0f), Models.HANDHELD);
    public static final Item ELEMENT_HOE = EWRegister.register("element_hoe", ElementToolItem.createHoe(-2, -0.5f), Models.HANDHELD);
    public static final Item ELEMENT_BOW = EWRegister.register("element_bow", new ElementBowItem(), null);

    public static void register() {
        LOGGER.info("Loading Items");
    }
}
