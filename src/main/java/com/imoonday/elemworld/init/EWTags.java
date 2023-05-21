package com.imoonday.elemworld.init;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWTags {

    public static final TagKey<Item> BASE_ELEMENT_FRAGMENTS = TagKey.of(RegistryKeys.ITEM, id("base_element_fragments"));
    public static final TagKey<Item> ADVANCED_ELEMENT_FRAGMENTS = TagKey.of(RegistryKeys.ITEM, id("advanced_element_fragments"));
    public static final TagKey<Item> RARE_ELEMENT_FRAGMENTS = TagKey.of(RegistryKeys.ITEM, id("rare_element_fragments"));
    public static final TagKey<Item> ELEMENT_FRAGMENTS = TagKey.of(RegistryKeys.ITEM, id("element_fragments"));
    public static final TagKey<Item> ELEMENT_TOOLS_AND_WEAPONS = TagKey.of(RegistryKeys.ITEM, id("element_tools_and_weapons"));
    public static final TagKey<Item> ELEMENT_STAFFS = TagKey.of(RegistryKeys.ITEM, id("element_staffs"));
}
