package com.imoonday.elemworld;

import com.google.common.collect.ImmutableSet;
import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.init.EWBlocks;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.VanillaRecipeProvider;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.imoonday.elemworld.init.EWBlocks.ELEMENT_SMITHING_TABLE;
import static com.imoonday.elemworld.init.EWItems.*;
import static com.imoonday.elemworld.tags.EWTags.*;

public class EWDataGeneration implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(BlockLootTable::new);
        pack.addProvider(BlockTag::new);
        pack.addProvider(ItemTag::new);
        pack.addProvider(Recipe::new);
        pack.addProvider(Model::new);
    }

    private static class Recipe extends FabricRecipeProvider {

        public Recipe(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generate(Consumer<RecipeJsonProvider> exporter) {
            ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ELEMENT_SMITHING_TABLE).input('1', ItemTags.PLANKS).input('2', ELEMENT_INGOT).pattern("22").pattern("11").pattern("11").criterion(hasItem(ELEMENT_INGOT), VanillaRecipeProvider.conditionsFromItem(ELEMENT_INGOT)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ELEMENT_DETECTOR).input('1', ELEMENT_INGOT).input('2', ELEMENT_STICK).pattern("111").pattern("121").pattern(" 2 ").criterion(hasItem(ELEMENT_INGOT), VanillaRecipeProvider.conditionsFromItem(ELEMENT_INGOT)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BASE_ELEMENT_FRAGMENT, 2).input('1', BASE_ELEMENT_FRAGMENTS).pattern("111").pattern("111").pattern("111").criterion("has_base_element_fragments", VanillaRecipeProvider.conditionsFromTag(BASE_ELEMENT_FRAGMENTS)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ADVANCED_ELEMENT_FRAGMENT, 2).input('1', ADVANCED_ELEMENT_FRAGMENTS).input('2', BASE_ELEMENT_FRAGMENT).pattern("111").pattern("121").pattern("111").criterion(hasItem(BASE_ELEMENT_FRAGMENT), VanillaRecipeProvider.conditionsFromItem(BASE_ELEMENT_FRAGMENT)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, RARE_ELEMENT_FRAGMENT, 2).input('1', RARE_ELEMENT_FRAGMENTS).input('2', ADVANCED_ELEMENT_FRAGMENT).pattern("111").pattern("121").pattern("111").criterion(hasItem(ADVANCED_ELEMENT_FRAGMENT), VanillaRecipeProvider.conditionsFromItem(ADVANCED_ELEMENT_FRAGMENT)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ELEMENT_STICK, 4).input('1', ELEMENT_FRAGMENTS).pattern("1").pattern("1").criterion("has_element_fragments", VanillaRecipeProvider.conditionsFromTag(ELEMENT_FRAGMENTS)).offerTo(exporter);
        }
    }

    private static class BlockTag extends FabricTagProvider<Block> {

        public BlockTag(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.BLOCK, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ELEMENT_SMITHING_TABLE);
        }
    }

    private static class ItemTag extends FabricTagProvider<Item> {

        public ItemTag(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.ITEM, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            ImmutableSet<Element> elements = Element.getRegistrySet(false);
            elements.stream().filter(element -> element.rareLevel == 1).forEach(element -> getOrCreateTagBuilder(BASE_ELEMENT_FRAGMENTS).add(element.getFragmentItem()));
            elements.stream().filter(element -> element.rareLevel == 2).forEach(element -> getOrCreateTagBuilder(ADVANCED_ELEMENT_FRAGMENTS).add(element.getFragmentItem()));
            elements.stream().filter(element -> element.rareLevel == 3).forEach(element -> getOrCreateTagBuilder(RARE_ELEMENT_FRAGMENTS).add(element.getFragmentItem()));
            elements.forEach(element -> getOrCreateTagBuilder(ELEMENT_FRAGMENTS).add(element.getFragmentItem()));
        }
    }

    private static class BlockLootTable extends FabricBlockLootTableProvider {

        protected BlockLootTable(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        @Override
        public void generate() {
            EWBlocks.DROPS.forEach((block, consumer) -> consumer.accept(this));
        }
    }

    private static class Model extends FabricModelProvider {

        public Model(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
            blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(ELEMENT_SMITHING_TABLE, Models.CUBE.upload(ELEMENT_SMITHING_TABLE, new TextureMap().put(TextureKey.PARTICLE, TextureMap.getSubId(ELEMENT_SMITHING_TABLE, "_front")).put(TextureKey.DOWN, TextureMap.getSubId(ELEMENT_SMITHING_TABLE, "_bottom")).put(TextureKey.UP, TextureMap.getSubId(ELEMENT_SMITHING_TABLE, "_top")).put(TextureKey.NORTH, TextureMap.getSubId(ELEMENT_SMITHING_TABLE, "_front")).put(TextureKey.SOUTH, TextureMap.getSubId(ELEMENT_SMITHING_TABLE, "_front")).put(TextureKey.EAST, TextureMap.getSubId(ELEMENT_SMITHING_TABLE, "_side")).put(TextureKey.WEST, TextureMap.getSubId(ELEMENT_SMITHING_TABLE, "_side")), blockStateModelGenerator.modelCollector)));
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            MODELS.forEach(itemModelGenerator::register);
        }
    }
}
