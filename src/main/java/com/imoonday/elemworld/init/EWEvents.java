package com.imoonday.elemworld.init;

import com.imoonday.elemworld.items.ElementDetectorItem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;

import java.util.HashMap;

import static com.imoonday.elemworld.init.EWIdentifiers.CHANGE_LEVEL;
import static com.imoonday.elemworld.init.EWIdentifiers.DISPLAY_SCREEN;

public class EWEvents {

    public static void register() {
        registerGlobalReceiver();
        keepElementsOndeath();
        registerStaffLootables();
    }

    private static void registerStaffLootables() {
        EWItems.getAllStaffs().forEach(item -> LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            item.getLootables(new HashMap<>()).forEach((identifier, chance) -> {
                if (identifier.equals(id)) {
                    LootPool.Builder poolBuilder = LootPool.builder()
                            .rolls(ConstantLootNumberProvider.create(1))
                            .with(ItemEntry.builder(item)
                                    .conditionally(RandomChanceLootCondition.builder(chance)));

                    tableBuilder.pool(poolBuilder);
                }
            });
        }));
    }

    private static void keepElementsOndeath() {
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if (oldPlayer.world.getGameRules().getBoolean(EWGameRules.KEEP_ELEMENTS)) {
                newPlayer.setElements(oldPlayer.getElements());
            }
        });
    }

    private static void registerGlobalReceiver() {
        registerServer(DISPLAY_SCREEN, (server, player, handler, buf, responseSender) -> server.execute(() -> {
            if (player.currentScreenHandler == player.playerScreenHandler) {
                ElementDetectorItem.openScreen(player, player);
            }
        }));

        registerClient(CHANGE_LEVEL, (client, handler, buf, responseSender) -> {
            int count = buf.readInt();
            client.execute(() -> {
                if (client.player != null) {
                    client.player.experienceLevel += count;
                }
            });
        });
    }

    private static void registerServer(Identifier channelName, ServerPlayNetworking.PlayChannelHandler channelHandler) {
        ServerPlayNetworking.registerGlobalReceiver(channelName, channelHandler);
    }

    private static void registerClient(Identifier channelName, ClientPlayNetworking.PlayChannelHandler channelHandler) {
        ClientPlayNetworking.registerGlobalReceiver(channelName, channelHandler);
    }
}
