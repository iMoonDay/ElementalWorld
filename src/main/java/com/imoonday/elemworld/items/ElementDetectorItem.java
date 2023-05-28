package com.imoonday.elemworld.items;

import com.imoonday.elemworld.screens.handler.ElementDetailsScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;

public class ElementDetectorItem extends Item {
    public ElementDetectorItem() {
        super(new Settings().maxCount(1));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (user.world.isClient) {
            return ActionResult.SUCCESS;
        }
        openScreen(user, entity);
        return ActionResult.CONSUME;
    }

    public static void openScreen(PlayerEntity user, LivingEntity entity) {
        user.openHandledScreen(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                buf.writeInt(entity.getId());
            }

            @Override
            public Text getDisplayName() {
                return entity.getName();
            }

            @Override
            public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new ElementDetailsScreenHandler(syncId, playerInventory, entity);
            }
        });
    }
}
