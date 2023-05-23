package com.imoonday.elemworld.init;

import com.imoonday.elemworld.gui.ElementRendererGui;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;
import static com.imoonday.elemworld.init.EWIdentifiers.DISPLAY_SCREEN;

@Environment(EnvType.CLIENT)
public class EWKeyBindings {

    public static final KeyBinding DISPLAY = register(EWTranslationKeys.KEY_DISPLAY, GLFW.GLFW_KEY_O, () -> ClientPlayNetworking.send(DISPLAY_SCREEN, PacketByteBufs.empty()));
    public static final KeyBinding TOGGLE_VISIBILITY = register(EWTranslationKeys.KEY_TOGGLE_VISIBILITY, GLFW.GLFW_KEY_I, ElementRendererGui.INSTANCE::toggleVisibility);

    public static void registerClient() {
        LOGGER.info("Loading KeyBindings");
    }

    public static KeyBinding register(String translationKey, int code, Runnable whilePressed) {
        KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(translationKey,
                InputUtil.Type.KEYSYM,
                code,
                EWTranslationKeys.KEY_CATEGORY));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) whilePressed.run();
        });
        return keyBinding;
    }
}
