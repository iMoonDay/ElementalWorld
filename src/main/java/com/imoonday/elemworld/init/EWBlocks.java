package com.imoonday.elemworld.init;

import com.imoonday.elemworld.blocks.ElementModifierBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.elemworld.ElementalWorld.id;

public class EWBlocks {

    public static final ElementModifierBlock ELEMENT_MODIFIER_BLOCK = register("element_modifier",new ElementModifierBlock());

    public static void register(){}

    static <T extends Block> T register(String id, T block) {
        registerBlockItem(id, new BlockItem(block, new Item.Settings()));
        return registerBlock(id, block);
    }

    static <T extends Block> T registerBlock(String id, T block) {
        return Registry.register(Registries.BLOCK, id(id), block);
    }

    static void registerBlockItem(String id, Item blockItem) {
        ItemGroupEvents.modifyEntriesEvent(EWItemGroups.ELEMENTAL_WORLD).register(content -> content.add(blockItem.getDefaultStack()));
        Registry.register(Registries.ITEM, id(id), blockItem);
    }
}
