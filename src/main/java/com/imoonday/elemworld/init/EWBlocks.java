package com.imoonday.elemworld.init;

import com.google.common.collect.ImmutableMap;
import com.imoonday.elemworld.blocks.ElementSmithingTableBlock;
import com.imoonday.elemworld.blocks.ElementalAltarBlock;
import com.imoonday.elemworld.blocks.entities.ElementalAltarBlockEntity;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.data.client.Model;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

import java.util.*;
import java.util.function.Consumer;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;
import static com.imoonday.elemworld.ElementalWorldData.addTranslation;
import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWBlocks {

    private static final Map<Block, Consumer<BlockLootTableGenerator>> BLOCK_DROPS = new HashMap<>();
    private static final Map<Block, List<TagKey<Block>>> BLOCK_TAGS = new HashMap<>();

    public static final Block ELEMENT_SMITHING_TABLE = register("element_smithing_table", new ElementSmithingTableBlock(), "Element Smithing Table", "元素锻造台", BlockTags.AXE_MINEABLE);
    public static final Block ELEMENTAL_ALTAR = register("elemental_altar", new ElementalAltarBlock(), "Elemental Altar", "元素祭坛", BlockTags.PICKAXE_MINEABLE);
    public static final BlockEntityType<ElementalAltarBlockEntity> ELEMENTAL_ALTAR_BLOCK_ENTITY = registerBlockEntity("elemental_altar", ElementalAltarBlockEntity::new, ELEMENTAL_ALTAR);

    public static void register() {
        LOGGER.info("Loading Blocks");
    }

    public static void registerClient() {
        BlockEntityRendererFactories.register(ELEMENTAL_ALTAR_BLOCK_ENTITY, ElementalAltarBlockEntity.Renderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ELEMENTAL_ALTAR, RenderLayer.getCutout());
    }

    @SafeVarargs
    public static <T extends Block> T register(String id, T block, String en_us, String zh_cn, TagKey<Block>... tags) {
        EWItems.register(id, new BlockItem(block, new FabricItemSettings()), (Model) null, null, null);
        return registerBlock(id, block, generator -> generator.addDrop(block), en_us, zh_cn, tags);
    }

    @SafeVarargs
    public static <T extends Block> T register(String id, T block, ItemConvertible[] drops, String en_us, String zh_cn, TagKey<Block>... tags) {
        EWItems.register(id, new BlockItem(block, new FabricItemSettings()), (Model) null, null, null);
        return registerBlock(id, block, generator -> Arrays.stream(drops).forEach(itemConvertible -> generator.addDrop(block, itemConvertible)), en_us, zh_cn, tags);
    }

    @SafeVarargs
    public static <T extends Block> T registerBlock(String id, T block, Consumer<BlockLootTableGenerator> addDrop, String en_us, String zh_cn, TagKey<Block>... tags) {
        BLOCK_DROPS.put(block, addDrop);
        if (tags != null && tags.length > 0) {
            List<TagKey<Block>> list = Arrays.asList(tags);
            list.removeIf(Objects::isNull);
            if (list.size() > 0) {
                BLOCK_TAGS.put(block, list);
            }
        }
        addTranslation(block, en_us, zh_cn);
        return Registry.register(Registries.BLOCK, id(id), block);
    }

    public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block block) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, id(id), FabricBlockEntityTypeBuilder.create(factory, block).build());
    }

    public static ImmutableMap<Block, Consumer<BlockLootTableGenerator>> getAllDrops() {
        return ImmutableMap.copyOf(BLOCK_DROPS);
    }

    public static ImmutableMap<Block, List<TagKey<Block>>> getAllTags() {
        return ImmutableMap.copyOf(BLOCK_TAGS);
    }
}
