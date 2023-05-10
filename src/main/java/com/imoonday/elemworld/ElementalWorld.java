package com.imoonday.elemworld;

import com.imoonday.elemworld.api.EWRegistry;
import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.init.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElementalWorld implements ModInitializer {

    public static final String MOD_ID = "elemworld";
    public static final Logger LOGGER = LoggerFactory.getLogger("ElementalWorld");
    public static final String VERSION = "v0.1";

    @Override
    public void onInitialize() {
        EWElements.register();
        EWCommands.register();
        EWItemGroups.register();
        EWItems.register();
        EWBlocks.register();
        EWEffects.register();
        EWPotions.register();
        EWEvents.register();
        EWGameRules.register();
        EWBiomes.register();
        Element.register();
    }
}
