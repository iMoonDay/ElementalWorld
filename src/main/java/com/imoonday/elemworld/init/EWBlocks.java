package com.imoonday.elemworld.init;

import com.imoonday.elemworld.blocks.ElementSmithingTableBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;
import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWBlocks {

    public static final Block ELEMENT_SMITHING_TABLE = register("element_smithing_table", new ElementSmithingTableBlock());

    public static void register() {
        LOGGER.info("Loading Blocks");
    }

    static Block register(String id, Block block) {
        registerBlockItem(id, new BlockItem(block, new Item.Settings()));
        return registerBlock(id, block);
    }

    static Block registerBlock(String id, Block block) {
        return Registry.register(Registries.BLOCK, id(id), block);
    }

    static void registerBlockItem(String id, Item blockItem) {
        ItemGroupEvents.modifyEntriesEvent(EWItemGroups.ELEMENTAL_WORLD).register(content -> content.add(blockItem.getDefaultStack()));
        Registry.register(Registries.ITEM, id(id), blockItem);
    }
}
