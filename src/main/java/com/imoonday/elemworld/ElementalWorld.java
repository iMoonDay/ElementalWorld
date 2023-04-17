package com.imoonday.elemworld;

import com.imoonday.elemworld.effect.ElementEffect;
import com.imoonday.elemworld.init.EWCommands;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElementalWorld implements ModInitializer {

    public static final String MOD_ID = "elemworld";
    public static final Logger LOGGER = LoggerFactory.getLogger("ElementalWorld");
    public static final String VERSION = "v0.1";

    @Override
    public void onInitialize() {
        ElementEffect.register();
        EWCommands.register();
    }

    public static Identifier id(String id) {
        return new Identifier(MOD_ID, id);
    }
}
