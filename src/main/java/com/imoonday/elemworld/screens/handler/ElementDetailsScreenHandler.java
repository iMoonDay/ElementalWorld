package com.imoonday.elemworld.screens.handler;

import com.imoonday.elemworld.init.EWScreens;
import com.imoonday.elemworld.interfaces.EWLivingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

public class ElementDetailsScreenHandler extends ScreenHandler {

    private final PlayerEntity player;
    private final LivingEntity livingEntity;

    public ElementDetailsScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, playerInventory.player.world.getEntityById(buf.readInt()) instanceof LivingEntity living ? living : null);
    }

    public ElementDetailsScreenHandler(int syncId, PlayerInventory playerInventory, LivingEntity livingEntity) {
        super(EWScreens.ELEMENT_DETAILS_SCREEN_HANDLER, syncId);
        this.player = playerInventory.player;
        this.livingEntity = livingEntity;
        int y;
        int x;
        int index = 0;
        for (int weight : new int[]{8, 44, 62, 80, 98, 134, 152}) {
            this.addSlot(new EquipmentSlot(getEquipments(livingEntity), index++, weight, 54));
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

    @NotNull
    private static SimpleInventory getEquipments(LivingEntity entity) {
        SimpleInventory equipments = new SimpleInventory(7);
        int slot = 0;
        Item item;
        if (entity instanceof PlayerEntity) {
            item = Items.PLAYER_HEAD;
        } else if (entity instanceof ZombieEntity) {
            item = Items.ZOMBIE_HEAD;
        } else if (entity instanceof SkeletonEntity) {
            item = Items.SKELETON_SKULL;
        } else if (entity instanceof CreeperEntity) {
            item = Items.CREEPER_HEAD;
        } else if (entity instanceof WitherSkeletonEntity) {
            item = Items.WITHER_SKELETON_SKULL;
        } else if (entity instanceof EnderDragonEntity) {
            item = Items.DRAGON_HEAD;
        } else {
            item = Items.ARMOR_STAND;
        }
        ItemStack stack = new ItemStack(item);
        stack.setElements(((EWLivingEntity) entity).getElements());
        if (entity instanceof PlayerEntity playerEntity) {
            stack.getOrCreateNbt().putString("SkullOwner", playerEntity.getName().getString());
        }
        stack.setCustomName(entity.getName().copy().formatted(Formatting.WHITE, Formatting.BOLD, Formatting.UNDERLINE));
        equipments.setStack(slot++, stack);
        equipments.setStack(slot++, entity.getEquippedStack(net.minecraft.entity.EquipmentSlot.HEAD));
        equipments.setStack(slot++, entity.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST));
        equipments.setStack(slot++, entity.getEquippedStack(net.minecraft.entity.EquipmentSlot.LEGS));
        equipments.setStack(slot++, entity.getEquippedStack(net.minecraft.entity.EquipmentSlot.FEET));
        equipments.setStack(slot++, entity.getEquippedStack(net.minecraft.entity.EquipmentSlot.MAINHAND));
        equipments.setStack(slot, entity.getEquippedStack(net.minecraft.entity.EquipmentSlot.OFFHAND));
        return equipments;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
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

    public LivingEntity getLivingEntity() {
        return livingEntity;
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
