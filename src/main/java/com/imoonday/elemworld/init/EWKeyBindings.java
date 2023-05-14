package com.imoonday.elemworld.init;

import com.imoonday.elemworld.ElementalWorldData;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;
import static com.imoonday.elemworld.init.EWIdentifiers.DISPLAY_SCREEN;

public class EWKeyBindings {

    public static final String CATEGORY = "key.categories.elemworld";
    public static final String DISPLAY = "key.elemworld.display";

    public static void register(){
        ElementalWorldData.addCustomTranslation(CATEGORY, "Elemental World", "元素世界");
        ElementalWorldData.addCustomTranslation(DISPLAY, "Open the element screen", "打开元素界面");
        LOGGER.info("Loading KeyBindings");
    }

    public static void registerClient() {
        KeyBinding display = KeyBindingHelper.registerKeyBinding(new KeyBinding(DISPLAY,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (display.wasPressed()) {
                ClientPlayNetworking.send(DISPLAY_SCREEN, PacketByteBufs.empty());
            }
        });
    }
}
