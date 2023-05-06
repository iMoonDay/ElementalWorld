package com.imoonday.elemworld;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.VanillaRecipeProvider;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.imoonday.elemworld.init.EWBlocks.ELEMENT_SMITHING_TABLE;
import static com.imoonday.elemworld.init.EWItems.*;

public class EWDataGeneration implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(BlockLootTable::new);
        pack.addProvider(Recipe::new);
        pack.addProvider(Tag::new);
    }

    private static class Tag extends FabricTagProvider<Block> {

        public Tag(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.BLOCK, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ELEMENT_SMITHING_TABLE);
        }
    }

    private static class BlockLootTable extends FabricBlockLootTableProvider {

        protected BlockLootTable(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        @Override
        public void generate() {
            addDrop(ELEMENT_SMITHING_TABLE);
        }
    }

    private static class Recipe extends FabricRecipeProvider {

        public Recipe(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generate(Consumer<RecipeJsonProvider> exporter) {
            ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ELEMENT_SMITHING_TABLE).input('1', ItemTags.PLANKS).input('2', ELEMENT_INGOT).pattern("22").pattern("11").pattern("11").criterion("has_element_ingot", VanillaRecipeProvider.conditionsFromItem(ELEMENT_INGOT)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ELEMENT_DETECTOR).input('1', ELEMENT_INGOT).input('2', ELEMENT_STICK).pattern("111").pattern("121").pattern(" 2 ").criterion("has_element_ingot", VanillaRecipeProvider.conditionsFromItem(ELEMENT_INGOT)).offerTo(exporter);
        }
    }
}
