package com.imoonday.elemworld.init;

import com.imoonday.elemworld.blocks.ElementSmithingTableBlock;
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

    public static final Block ELEMENT_SMITHING_TABLE = register("element_smithing_table", new ElementSmithingTableBlock());
    public static final HashMap<Block, Consumer<BlockLootTableGenerator>> BLOCK_DROPS = new HashMap<>();

    public static void register() {
        LOGGER.info("Loading Blocks");
    }

    public static Block register(String id, Block block) {
        EWItems.register(id, (Item) new BlockItem(block, new Item.Settings()));
        return registerBlock(id, block, generator -> generator.addDrop(block));
    }

    public static Block register(String id, Block block, ItemConvertible... drops) {
        EWItems.register(id, (Item) new BlockItem(block, new Item.Settings()));
        return registerBlock(id, block, generator -> Arrays.stream(drops).toList().forEach(itemConvertible -> generator.addDrop(block, itemConvertible)));
    }

    public static Block registerBlock(String id, Block block, Consumer<BlockLootTableGenerator> addDrop) {
        BLOCK_DROPS.put(block, addDrop);
        return Registry.register(Registries.BLOCK, id(id), block);
    }
}
