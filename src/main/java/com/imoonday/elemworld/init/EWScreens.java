package com.imoonday.elemworld.init;

import com.imoonday.elemworld.screens.ElementDetailsScreen;
import com.imoonday.elemworld.screens.ElementSmithingScreen;
import com.imoonday.elemworld.screens.handler.ElementDetailsScreenHandler;
import com.imoonday.elemworld.screens.handler.ElementSmithingScreenHandler;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;

import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWScreens {

    public static final ScreenHandlerType<ElementSmithingScreenHandler> ELEMENT_SMITHING_SCREEN_HANDLER;
    public static final ScreenHandlerType<ElementDetailsScreenHandler> ELEMENT_DETAILS_SCREEN_HANDLER;

    static {
        ELEMENT_SMITHING_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(id("modify_elements"), ElementSmithingScreenHandler::new);
        ELEMENT_DETAILS_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(id("element_details"), ElementDetailsScreenHandler::new);
    }

    public static void registerClient() {
        ScreenRegistry.register(ELEMENT_SMITHING_SCREEN_HANDLER, ElementSmithingScreen::new);
        ScreenRegistry.register(ELEMENT_DETAILS_SCREEN_HANDLER, ElementDetailsScreen::new);
    }
}
