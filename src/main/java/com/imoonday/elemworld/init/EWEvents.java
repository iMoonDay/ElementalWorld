package com.imoonday.elemworld.init;

import com.imoonday.elemworld.items.ElementDetectorItem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

import static com.imoonday.elemworld.init.EWIdentifiers.DISPLAY_SCREEN;

public class EWEvents {

    public static void register() {
        registerServer(DISPLAY_SCREEN, (server, player, handler, buf, responseSender) -> server.execute(() -> {
            if (player.currentScreenHandler == player.playerScreenHandler) {
                ElementDetectorItem.openScreen(player, player);
            }
        }));
    }

    private static void registerServer(Identifier channelName, ServerPlayNetworking.PlayChannelHandler channelHandler) {
        ServerPlayNetworking.registerGlobalReceiver(channelName, channelHandler);
    }

    private static void registerClient(Identifier channelName, ClientPlayNetworking.PlayChannelHandler channelHandler) {
        ClientPlayNetworking.registerGlobalReceiver(channelName, channelHandler);
    }
}
