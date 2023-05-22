package com.imoonday.elemworld;

import com.imoonday.elemworld.api.Translation;
import com.imoonday.elemworld.init.EWBlocks;
import com.imoonday.elemworld.init.EWItems;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.VanillaRecipeProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.imoonday.elemworld.init.EWBlocks.ELEMENT_SMITHING_TABLE;
import static com.imoonday.elemworld.init.EWItems.*;
import static com.imoonday.elemworld.init.EWTags.*;
import static net.minecraft.item.Items.*;
import static net.minecraft.recipe.book.RecipeCategory.*;

public class ElementalWorldData implements DataGeneratorEntrypoint {

    private static final Set<Translation<?>> TRANSLATIONS = new HashSet<>();

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(BlockLootTable::new);
        pack.addProvider(BlockTag::new);
        pack.addProvider(ItemTag::new);
        pack.addProvider(Recipe::new);
        pack.addProvider(Model::new);
        HashSet<String> languageCodes = new HashSet<>();
        TRANSLATIONS.forEach(translation -> languageCodes.addAll(translation.getLanguageCodes()));
        languageCodes.forEach(languageCode -> pack.addProvider((FabricDataGenerator.Pack.Factory<Language>) output -> new Language(output, languageCode)));
    }

    private static class Recipe extends FabricRecipeProvider {

        public Recipe(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generate(Consumer<RecipeJsonProvider> exporter) {
            ShapedRecipeJsonBuilder.create(DECORATIONS, ELEMENT_SMITHING_TABLE).input('1', ItemTags.PLANKS).input('2', ELEMENT_INGOT).pattern("22").pattern("11").pattern("11").criterion(hasItem(ELEMENT_INGOT), VanillaRecipeProvider.conditionsFromItem(ELEMENT_INGOT)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(MISC, ELEMENT_DETECTOR).input('1', ELEMENT_INGOT).input('2', ELEMENT_STICK).pattern("111").pattern("121").pattern(" 2 ").criterion(hasItem(ELEMENT_INGOT), VanillaRecipeProvider.conditionsFromItem(ELEMENT_INGOT)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(MISC, BASE_ELEMENT_FRAGMENT, 2).input('1', BASE_ELEMENT_FRAGMENTS).pattern("111").pattern("111").pattern("111").criterion("has_base_element_fragments", VanillaRecipeProvider.conditionsFromTag(BASE_ELEMENT_FRAGMENTS)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(MISC, ADVANCED_ELEMENT_FRAGMENT, 2).input('1', ADVANCED_ELEMENT_FRAGMENTS).input('2', BASE_ELEMENT_FRAGMENT).pattern("111").pattern("121").pattern("111").criterion(hasItem(BASE_ELEMENT_FRAGMENT), VanillaRecipeProvider.conditionsFromItem(BASE_ELEMENT_FRAGMENT)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(MISC, RARE_ELEMENT_FRAGMENT, 2).input('1', RARE_ELEMENT_FRAGMENTS).input('2', ADVANCED_ELEMENT_FRAGMENT).pattern("111").pattern("121").pattern("111").criterion(hasItem(ADVANCED_ELEMENT_FRAGMENT), VanillaRecipeProvider.conditionsFromItem(ADVANCED_ELEMENT_FRAGMENT)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(MISC, ELEMENT_STICK, 4).input('1', ELEMENT_FRAGMENTS).pattern("1").pattern("1").criterion("has_element_fragments", VanillaRecipeProvider.conditionsFromTag(ELEMENT_FRAGMENTS)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(COMBAT, ELEMENT_SWORD).input('1', ELEMENT_STICK).input('2', ELEMENT_INGOT).pattern("2").pattern("2").pattern("1").criterion(hasItem(ELEMENT_INGOT), VanillaRecipeProvider.conditionsFromItem(ELEMENT_INGOT)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(TOOLS, ELEMENT_PICKAXE).input('1', ELEMENT_STICK).input('2', ELEMENT_INGOT).pattern("222").pattern(" 1 ").pattern(" 1 ").criterion(hasItem(ELEMENT_INGOT), VanillaRecipeProvider.conditionsFromItem(ELEMENT_INGOT)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(TOOLS, ELEMENT_AXE).input('1', ELEMENT_STICK).input('2', ELEMENT_INGOT).pattern("22").pattern("21").pattern(" 1").criterion(hasItem(ELEMENT_INGOT), VanillaRecipeProvider.conditionsFromItem(ELEMENT_INGOT)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(TOOLS, ELEMENT_SHOVEL).input('1', ELEMENT_STICK).input('2', ELEMENT_INGOT).pattern("2").pattern("1").pattern("1").criterion(hasItem(ELEMENT_INGOT), VanillaRecipeProvider.conditionsFromItem(ELEMENT_INGOT)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(TOOLS, ELEMENT_HOE).input('1', ELEMENT_STICK).input('2', ELEMENT_INGOT).pattern("22").pattern(" 1").pattern(" 1").criterion(hasItem(ELEMENT_INGOT), VanillaRecipeProvider.conditionsFromItem(ELEMENT_INGOT)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(COMBAT, ELEMENT_BOW).input('1', ELEMENT_STICK).input('2', STRING).pattern(" 12").pattern("1 2").pattern(" 12").criterion(hasItem(ELEMENT_STICK), VanillaRecipeProvider.conditionsFromItem(ELEMENT_STICK)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(MISC, ELEMENT_INGOT).input('1', IRON_INGOT).input('2', ELEMENT_FRAGMENTS).pattern(" 2 ").pattern("212").pattern(" 2 ").criterion("has_element_fragments", VanillaRecipeProvider.conditionsFromTag(ELEMENT_FRAGMENTS)).offerTo(exporter);
            ShapedRecipeJsonBuilder.create(COMBAT, UMBRELLA).input('1', BLUE_WOOL).input('2', STICK).input('3', YELLOW_CARPET).pattern(" 1 ").pattern("121").pattern(" 33").criterion("has_wools", VanillaRecipeProvider.conditionsFromTag(ItemTags.WOOL)).offerTo(exporter);
        }
    }

    private static class BlockTag extends FabricTagProvider<Block> {

        public BlockTag(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.BLOCK, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            EWBlocks.getAllTags().forEach((block, tagKeys) -> tagKeys.forEach(tagKey -> getOrCreateTagBuilder(tagKey).add(block)));
        }
    }

    private static class ItemTag extends FabricTagProvider<Item> {

        public ItemTag(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.ITEM, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            EWItems.getAllTags().forEach((item, tagKeys) -> tagKeys.forEach(itemTagKey -> getOrCreateTagBuilder(itemTagKey).add(item)));
        }
    }

    private static class BlockLootTable extends FabricBlockLootTableProvider {

        protected BlockLootTable(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        @Override
        public void generate() {
            EWBlocks.getAllDrops().forEach((block, consumer) -> consumer.accept(this));
        }
    }

    private static class Model extends FabricModelProvider {

        public Model(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
            blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(ELEMENT_SMITHING_TABLE, Models.CUBE.upload(ELEMENT_SMITHING_TABLE, new TextureMap().put(TextureKey.PARTICLE, TextureMap.getSubId(ELEMENT_SMITHING_TABLE, "_front")).put(TextureKey.DOWN, TextureMap.getSubId(ELEMENT_SMITHING_TABLE, "_bottom")).put(TextureKey.UP, TextureMap.getSubId(ELEMENT_SMITHING_TABLE, "_top")).put(TextureKey.NORTH, TextureMap.getSubId(ELEMENT_SMITHING_TABLE, "_front")).put(TextureKey.SOUTH, TextureMap.getSubId(ELEMENT_SMITHING_TABLE, "_front")).put(TextureKey.EAST, TextureMap.getSubId(ELEMENT_SMITHING_TABLE, "_side")).put(TextureKey.WEST, TextureMap.getSubId(ELEMENT_SMITHING_TABLE, "_side")), blockStateModelGenerator.modelCollector)));
            blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(EWBlocks.ELEMENTAL_ALTAR, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(EWBlocks.ELEMENTAL_ALTAR))).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            getAllModels().forEach(itemModelGenerator::register);
        }
    }

    private static class Language extends FabricLanguageProvider {

        private final String languageCode;

        protected Language(FabricDataOutput dataOutput, String languageCode) {
            super(dataOutput, languageCode);
            this.languageCode = languageCode;
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder) {
            for (Translation<?> translation : TRANSLATIONS) {
                String content = translation.get(languageCode);
                if (content != null) {
                    Object instance = translation.getInstance();
                    try {
                        if (instance instanceof Item item) {
                            translationBuilder.add(item, content);
                        } else if (instance instanceof Block block) {
                            translationBuilder.add(block, content);
                        } else if (instance instanceof StatusEffect statusEffect) {
                            translationBuilder.add(statusEffect, content);
                        } else if (instance instanceof EntityType<?> entityType) {
                            translationBuilder.add(entityType, content);
                        } else if (instance instanceof Enchantment enchantment) {
                            translationBuilder.add(enchantment, content);
                        } else if (instance instanceof String translationKey) {
                            translationBuilder.add(translationKey, content);
                        } else if (instance instanceof ItemGroup itemGroup) {
                            translationBuilder.add(itemGroup, content);
                        } else if (instance instanceof EntityAttribute entityAttribute) {
                            translationBuilder.add(entityAttribute, content);
                        } else if (instance instanceof StatType<?> statType) {
                            translationBuilder.add(statType, content);
                        } else if (instance instanceof Identifier identifier) {
                            translationBuilder.add(identifier, content);
                        } else {
                            throw new IllegalStateException("Unsupported Type: " + instance);
                        }
                    } catch (Exception e) {
                        LOGGER.warn("Duplicate items found: " + instance);
                        LOGGER.warn(String.valueOf(e));
                    }
                }
            }

//            try {
//                Optional<Path> existingFilePath = dataOutput.getModContainer().findPath("assets/elemworld/lang/" + languageCode + ".json");
//                if (existingFilePath.isPresent()) {
//                    translationBuilder.add(existingFilePath.get());
//                } else {
//                    LOGGER.warn("No language files found!(" + languageCode + ")");
//                }
//            } catch (Exception e) {
//                LOGGER.warn("Failed to add existing language file!(" + languageCode + ")");
//                LOGGER.warn(String.valueOf(e));
//            }
        }
    }

    public static <T> Optional<Translation<?>> getTranslation(T t) {
        return TRANSLATIONS.stream().filter(translation -> translation.getInstance() == t).findFirst();
    }

    public static <T> void addTranslation(T t, String en_us, @Nullable String zh_cn) {
        if (en_us != null) {
            Translation<T> translation = new Translation<>(t, en_us);
            if (zh_cn != null) {
                translation.add("zh_cn", zh_cn);
            }
            TRANSLATIONS.add(translation);
        }
    }

    public static <T> void addCustomTranslation(T t, @NotNull String languageCode, @NotNull String content) {
        getTranslation(t).ifPresentOrElse(translation -> translation.add(languageCode, content), () -> TRANSLATIONS.add(new Translation<>(t, languageCode, content)));
    }
}
