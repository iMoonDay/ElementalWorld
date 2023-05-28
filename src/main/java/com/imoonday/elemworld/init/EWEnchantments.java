package com.imoonday.elemworld.init;

import com.imoonday.elemworld.ElementalWorld;
import com.imoonday.elemworld.ElementalWorldData;
import com.imoonday.elemworld.enchantments.ContinuousLaunchEnchantment;
import com.imoonday.elemworld.enchantments.InstantLaunchEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWEnchantments {

    public static final Enchantment INSTANT_LAUNCH = register("instant_launch", new InstantLaunchEnchantment(), "Instant Launch", "瞬间发射");
    public static final Enchantment CONTINUOUS_LAUNCH = register("continuous_launch", new ContinuousLaunchEnchantment(), "Continuous Launch", "连续发射");

    public static void register() {
        ElementalWorld.LOGGER.info("Loading Enchantments");
    }

    static <T extends Enchantment> T register(String id, T enchantment, String en_us, String zh_cn) {
        ElementalWorldData.addTranslation(enchantment, en_us, zh_cn);
        return Registry.register(Registries.ENCHANTMENT, id(id), enchantment);
    }
}
