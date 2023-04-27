package com.imoonday.elemworld.init;

import com.imoonday.elemworld.items.ElementDetectorItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;
import static com.imoonday.elemworld.ElementalWorld.id;

public class EWItems {

    public static final Item ELEMENT_DETECTOR = register("element_detector",new ElementDetectorItem());

    public static void register(){
        LOGGER.info("Loading Items");
    }

    static <T extends Item> T register(String id, T item) {
        ItemGroupEvents.modifyEntriesEvent(EWItemGroups.ELEMENTAL_WORLD).register(content -> content.add(item.getDefaultStack()));
        return Registry.register(Registries.ITEM, id(id), item);
    }
}
