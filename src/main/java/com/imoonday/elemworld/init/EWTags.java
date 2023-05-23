package com.imoonday.elemworld.init;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWTags {

    public static final TagKey<Item> BASE_ELEMENT_FRAGMENTS = registerItem("base_element_fragments");
    public static final TagKey<Item> ADVANCED_ELEMENT_FRAGMENTS = registerItem("advanced_element_fragments");
    public static final TagKey<Item> RARE_ELEMENT_FRAGMENTS = registerItem("rare_element_fragments");
    public static final TagKey<Item> ELEMENT_FRAGMENTS = registerItem("element_fragments");
    public static final TagKey<Item> ELEMENT_TOOLS_AND_WEAPONS = registerItem("element_tools_and_weapons");
    public static final TagKey<Item> ELEMENT_STAFFS = registerItem("element_staffs");

    public static TagKey<Item> registerItem(String name) {
        return TagKey.of(RegistryKeys.ITEM, id(name));
    }
}
