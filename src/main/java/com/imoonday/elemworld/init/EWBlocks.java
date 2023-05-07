package com.imoonday.elemworld.init;

import com.imoonday.elemworld.blocks.ElementSmithingTableBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;
import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWBlocks {

    public static final HashMap<Block, Consumer<BlockLootTableGenerator>> DROPS = new HashMap<>();

    public static final Block ELEMENT_SMITHING_TABLE = register("element_smithing_table", new ElementSmithingTableBlock());

    public static void register() {
        LOGGER.info("Loading Blocks");
    }

    static Block register(String id, Block block) {
        registerBlockItem(id, new BlockItem(block, new Item.Settings()));
        return registerBlock(id, block, generator -> generator.addDrop(block));
    }

    static Block register(String id, Block block, ItemConvertible... drops) {
        registerBlockItem(id, new BlockItem(block, new Item.Settings()));
        return registerBlock(id, block, generator -> Arrays.stream(drops).toList().forEach(itemConvertible -> generator.addDrop(block, itemConvertible)));
    }

    static Block registerBlock(String id, Block block, Consumer<BlockLootTableGenerator> addDrop) {
        DROPS.put(block, addDrop);
        return Registry.register(Registries.BLOCK, id(id), block);
    }

    static void registerBlockItem(String id, Item blockItem) {
        ItemGroupEvents.modifyEntriesEvent(EWItemGroups.ELEMENTAL_WORLD).register(content -> content.add(blockItem.getDefaultStack()));
        Registry.register(Registries.ITEM, id(id), blockItem);
    }
}
