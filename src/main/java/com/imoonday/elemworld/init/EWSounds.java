package com.imoonday.elemworld.init;

import com.imoonday.elemworld.ElementalWorldData;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;
import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWSounds {

    public static SoundEvent USE_STAFF = register("use_staff", "Use staff", "使用法杖");

    public static void register() {
        LOGGER.info("Loading Sounds");
    }

    public static SoundEvent register(String id, String en_us, String zh_cn) {
        ElementalWorldData.addTranslation("subtitles.elemworld." + id, en_us, zh_cn);
        return Registry.register(Registries.SOUND_EVENT, id(id), SoundEvent.of(id(id)));
    }
}
