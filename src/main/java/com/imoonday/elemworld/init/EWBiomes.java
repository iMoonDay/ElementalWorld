package com.imoonday.elemworld.init;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.biome.Biome;

import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWBiomes {

    public static final RegistryKey<Biome> WOOD_ELEMENT = register("wood_element");

    public static void register() {

    }

    public static RegistryKey<Biome> register(String id) {
        return RegistryKey.of(RegistryKeys.BIOME, id(id));
    }
}
