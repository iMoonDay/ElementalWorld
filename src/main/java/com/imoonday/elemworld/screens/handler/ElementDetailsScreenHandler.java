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
    private final ArrayList<Element> elements;

    public ElementDetailsScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(7));
    }

    public ElementDetailsScreenHandler(int syncId, PlayerInventory playerInventory, Inventory equipments) {
        super(EWScreens.ELEMENT_DETAILS_SCREEN_HANDLER, syncId);
        this.equipments = equipments;
        this.elements = equipments.getStack(0).getElements();
        checkSize(equipments, 7);
        int y;
        int x;
        int index = 0;
        this.addSlot(new EquipmentSlot(equipments, ++index, 8, 8));
        this.addSlot(new EquipmentSlot(equipments, ++index, 8, 26));
        this.addSlot(new EquipmentSlot(equipments, ++index, 153, 8));
        this.addSlot(new EquipmentSlot(equipments, ++index, 153, 26));
        this.addSlot(new EquipmentSlot(equipments, ++index, 8, 55));
        this.addSlot(new EquipmentSlot(equipments, ++index, 153, 55));
        for (y = 0; y < 3; ++y) {
            for (x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
        for (y = 0; y < 9; ++y) {
            this.addSlot(new Slot(playerInventory, y, 8 + y * 18, 142));
        }
    }

    public ArrayList<Element> getElements() {
        return elements;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.equipments.size()) {
                if (!this.insertItem(originalStack, this.equipments.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.equipments.size(), false)) {
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
