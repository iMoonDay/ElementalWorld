package com.imoonday.elemworld.screens.handler;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.init.EWScreens;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;

public class ElementDetailsScreenHandler extends ScreenHandler {

    private final Inventory equipments;
    private final PlayerEntity player;

    public ElementDetailsScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(7));
    }

    public ElementDetailsScreenHandler(int syncId, PlayerInventory playerInventory, Inventory equipments) {
        super(EWScreens.ELEMENT_DETAILS_SCREEN_HANDLER, syncId);
        this.equipments = equipments;
        this.player = playerInventory.player;
        checkSize(equipments, 7);
        int y;
        int x;
        int index = 0;
        for (int weight : new int[]{8, 44, 62, 80, 98, 134, 152}) {
            this.addSlot(new EquipmentSlot(equipments, index++, weight, 54));
        }
        for (y = 0; y < 3; ++y) {
            for (x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
        for (y = 0; y < 9; ++y) {
            this.addSlot(new Slot(playerInventory, y, 8 + y * 18, 142));
        }
    }

    public ArrayList<Element> getElements(int index) {
        return equipments.getStack(index).getElements();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.equipments.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot >= 7 && invSlot < 34) {
                if (!this.insertItem(originalStack, 34, this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 7, 34, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    private static class EquipmentSlot extends Slot {

        public EquipmentSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return false;
        }
    }
}
