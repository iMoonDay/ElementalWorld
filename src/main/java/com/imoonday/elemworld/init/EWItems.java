package com.imoonday.elemworld.init;

import com.imoonday.elemworld.items.ElementDetectorItem;
import com.imoonday.elemworld.items.ElementFragmentItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;
import static com.imoonday.elemworld.ElementalWorld.id;

public class EWItems {

    public static final Item ELEMENT_DETECTOR = register("element_detector", new ElementDetectorItem());
    public static final Item ELEMENT_STICK = register("element_stick");
    public static final Item BASE_ELEMENT_FRAGMENT = register("base_element_fragment", new ElementFragmentItem(1));
    public static final Item ADVANCED_ELEMENT_FRAGMENT = register("advanced_element_fragment", new ElementFragmentItem(2));
    public static final Item RARE_ELEMENT_FRAGMENT = register("rare_element_fragment", new ElementFragmentItem(3));
    public static final Item ELEMENT_INGOT = register("element_ingot");

    public static void register() {
        LOGGER.info("Loading Items");
    }

    public static <T extends Item> T register(String id, T item) {
        ItemGroupEvents.modifyEntriesEvent(EWItemGroups.ELEMENTAL_WORLD).register(content -> content.add(item.getDefaultStack()));
        return Registry.register(Registries.ITEM, id(id), item);
    }

    private static Item register(String id) {
        return register(id, new Item(new FabricItemSettings()));
    }

    private static Item register(String id, int count) {
        return register(id, new Item(new FabricItemSettings().maxCount(count)));
    }
}
