package com.imoonday.elemworld.init;

import com.imoonday.elemworld.screens.ElementDetailsScreen;
import com.imoonday.elemworld.screens.ElementSmithingTableScreen;
import com.imoonday.elemworld.screens.handler.ElementDetailsScreenHandler;
import com.imoonday.elemworld.screens.handler.ElementSmithingTableScreenHandler;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;

import static com.imoonday.elemworld.init.EWIdentifiers.id;

public class EWScreens {

    public static final ScreenHandlerType<ElementSmithingTableScreenHandler> ELEMENT_SMITHING_TABLE_SCREEN_HANDLER;
    public static final ScreenHandlerType<ElementDetailsScreenHandler> ELEMENT_DETAILS_SCREEN_HANDLER;

    static {
        ELEMENT_SMITHING_TABLE_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(id("modify_elements"), ElementSmithingTableScreenHandler::new);
        ELEMENT_DETAILS_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(id("element_details"), ElementDetailsScreenHandler::new);
    }

    public static void registerClient() {
        ScreenRegistry.register(ELEMENT_SMITHING_TABLE_SCREEN_HANDLER, ElementSmithingTableScreen::new);
        ScreenRegistry.register(ELEMENT_DETAILS_SCREEN_HANDLER, ElementDetailsScreen::new);
    }
}
