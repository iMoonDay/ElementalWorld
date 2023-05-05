package com.imoonday.elemworld.init;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static com.imoonday.elemworld.init.EWIdentifiers.DISPLAY_SCREEN;

public class EWKeyBindings {

    public static final String CATEGORY = "key.categories.elemworld";

    public static void registerClient() {
        KeyBinding display = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.elemworld.display",
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
