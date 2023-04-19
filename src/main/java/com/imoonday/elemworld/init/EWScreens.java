package com.imoonday.elemworld.init;

import com.imoonday.elemworld.screens.ModifyElementsScreen;
import com.imoonday.elemworld.screens.handler.ModifyElementsScreenHandler;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;

import static com.imoonday.elemworld.ElementalWorld.id;

public class EWScreens {

    public static final ScreenHandlerType<ModifyElementsScreenHandler> MODIFY_ELEMENTS_SCREEN_HANDLER;

    static {
        MODIFY_ELEMENTS_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(id("modify_elements"), ModifyElementsScreenHandler::new);
    }

    public static void registerClient(){
        ScreenRegistry.register(MODIFY_ELEMENTS_SCREEN_HANDLER, ModifyElementsScreen::new);
    }
}
