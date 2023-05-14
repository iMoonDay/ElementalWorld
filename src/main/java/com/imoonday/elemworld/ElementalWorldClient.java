package com.imoonday.elemworld;

import com.imoonday.elemworld.init.EWEntityRenderers;
import com.imoonday.elemworld.init.EWKeyBindings;
import com.imoonday.elemworld.init.EWScreens;
import com.imoonday.elemworld.items.ElementBowItem;
import net.fabricmc.api.ClientModInitializer;

public class ElementalWorldClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EWScreens.registerClient();
        EWKeyBindings.registerClient();
        ElementBowItem.registerClient();
        EWEntityRenderers.registerClient();
    }
}
