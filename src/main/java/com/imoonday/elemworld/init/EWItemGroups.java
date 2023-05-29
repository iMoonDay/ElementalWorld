package com.imoonday.elemworld.init;

import com.imoonday.elemworld.ElementalWorld;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.IntStream;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;
import static com.imoonday.elemworld.ElementalWorldData.addTranslation;
import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWItemGroups {

    public static final ItemGroup ELEMENTAL_WORLD = FabricItemGroup.builder(id("main"))
            .displayName(Text.translatable("group.elemworld." + "main"))
            .icon(() -> new ItemStack(EWItems.ELEMENT_INGOT)).build();

    public static void register() {
        addTranslation(ELEMENTAL_WORLD, "Elemental World", "元素世界");
        LOGGER.info("Loading ItemGroups");
    }

    public static void registerItems() {
        ItemGroupEvents.modifyEntriesEvent(ELEMENTAL_WORLD).register(entries -> {
            ItemGroup.DisplayContext displayContext = entries.getContext();
            EnumSet<EnchantmentTarget> set = EnumSet.allOf(EnchantmentTarget.class);
            displayContext.lookup().getOptionalWrapper(RegistryKeys.ENCHANTMENT).ifPresent(registryWrapper -> addAllLevelEnchantedBooks(entries, registryWrapper, set));
            displayContext.lookup().getOptionalWrapper(RegistryKeys.POTION).ifPresent(registryWrapper -> {
                addPotions(entries, Items.POTION);
                addPotions(entries, Items.SPLASH_POTION);
                addPotions(entries, Items.LINGERING_POTION);
                addPotions(entries, Items.TIPPED_ARROW);
            });
        });
    }

    private static void addPotions(ItemGroup.Entries entries, Item item) {
        for (Potion potion : EWPotions.getPotions()) {
            ItemStack stack = PotionUtil.setPotion(new ItemStack(item), potion);
            entries.add(stack, ItemGroup.StackVisibility.PARENT_TAB_ONLY);
        }
    }

    private static void addPotions(ItemGroup.Entries entries, RegistryWrapper<Potion> registryWrapper, Item item) {
        registryWrapper.streamEntries().filter(entry -> !entry.matchesKey(Potions.EMPTY_KEY) && entry.matches(potionRegistryKey -> potionRegistryKey.getValue().getNamespace().equals(ElementalWorld.MOD_ID))).map(entry -> PotionUtil.setPotion(new ItemStack(item), entry.value())).forEach(stack -> entries.add(stack, ItemGroup.StackVisibility.PARENT_TAB_ONLY));
    }

    private static void addAllLevelEnchantedBooks(ItemGroup.Entries entries, RegistryWrapper<Enchantment> registryWrapper, Set<EnchantmentTarget> enchantmentTargets) {
        registryWrapper.streamEntries().filter(enchantmentReference -> enchantmentReference.matches(enchantmentRegistryKey -> enchantmentRegistryKey.getValue().getNamespace().equals(ElementalWorld.MOD_ID))).map(RegistryEntry::value).filter(enchantment -> enchantmentTargets.contains(enchantment.target)).flatMap(enchantment -> IntStream.rangeClosed(enchantment.getMinLevel(), enchantment.getMaxLevel()).mapToObj(level -> EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, level)))).forEach(stack -> entries.add(stack, ItemGroup.StackVisibility.PARENT_TAB_ONLY));
    }
}
