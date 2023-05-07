package com.imoonday.elemworld.screens.handler;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.api.ElementEntry;
import com.imoonday.elemworld.init.EWBlocks;
import com.imoonday.elemworld.init.EWItems;
import com.imoonday.elemworld.init.EWScreens;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;

import java.util.HashSet;
import java.util.Set;

public class ElementSmithingScreenHandler extends ScreenHandler {

    public static final String LAST_RANDOM_ELEMENT_KEY = "LastRandomElement";
    public static final String RANDOM_COST_KEY = "RandomCost";
    private final Inventory result = new CraftingResultInventory();
    private final Inventory input = new SimpleInventory(2) {
        @Override
        public void markDirty() {
            super.markDirty();
            ElementSmithingScreenHandler.this.onContentChanged(this);
        }
    };
    private final ScreenHandlerContext context;
    private final PlayerEntity player;

    public ElementSmithingScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, ScreenHandlerContext.EMPTY);
    }

    public Inventory getResult() {
        return result;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public ElementSmithingScreenHandler(int syncId, PlayerInventory playerInventory, final ScreenHandlerContext context) {
        super(EWScreens.ELEMENT_SMITHING_SCREEN_HANDLER, syncId);
        this.context = context;
        this.player = playerInventory.player;
        checkSize(input, 2);
        int y;
        int x;
        this.addSlot(new Slot(this.input, 0, 27, 34) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isDamageable() || stack.isOf(EWItems.ELEMENT_BOOK);
            }
        });
        this.addSlot(new Slot(this.input, 1, 76, 34) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.DIAMOND) || stack.isOf(EWItems.ELEMENT_BOOK) || !stack.getStoredElementsIfBook().isEmpty();
            }
        });
        this.addSlot(new Slot(this.result, 2, 134, 34) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity) {
                return playerEntity.experienceLevel >= getRequiredLevel() || playerEntity.isCreative();
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                ElementSmithingScreenHandler.this.input.setStack(0, ItemStack.EMPTY);
                if (!getMaterial().isOf(Items.DIAMOND)) {
                    ElementSmithingScreenHandler.this.input.setStack(1, ItemStack.EMPTY);
                }
                stack.getOrCreateNbt().remove(LAST_RANDOM_ELEMENT_KEY);
                if (!player.isCreative()) {
                    player.experienceLevel -= ElementSmithingScreenHandler.this.getRequiredLevel();
                }
                player.playSound(SoundEvents.BLOCK_SMITHING_TABLE_USE, 1.0f, 1.0f);
                ElementSmithingScreenHandler.this.input.markDirty();
            }
        });
        for (y = 0; y < 3; ++y) {
            for (x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
        for (y = 0; y < 9; ++y) {
            this.addSlot(new Slot(playerInventory, y, 8 + y * 18, 142));
        }
    }

    public boolean cannotUseButton() {
        if (getMaterial().isEmpty() && !this.player.isCreative()) {
            return true;
        }
        if (!getMaterial().isEmpty() && !getMaterial().isOf(Items.DIAMOND)) {
            return true;
        }
        if (this.player.experienceLevel < 1 && !this.player.isCreative()) {
            return true;
        }
        if (getStack().isEmpty() || getStack().isOf(EWItems.ELEMENT_BOOK)) {
            return true;
        }
        return getStack().getStoredElementsIfBook().size() >= Element.getRegistrySet(false).size();
    }

    public int getRequiredLevel() {
        ItemStack stack = this.result.getStack(0);
        if (stack.isEmpty()) {
            return 0;
        }
        if (!stack.hasNbt()) {
            return 0;
        }
        Set<ElementEntry> entries = stack.getStoredElementsIfBook();
        float sum = 0;
        for (ElementEntry entry : entries) {
            if (entry.element().isInvalid()) {
                continue;
            }
            float i = entry.element().rareLevel * entries.size() * ((float) entry.level() / entry.element().maxLevel);
            sum += i;
        }
        int randomCost = stack.getOrCreateNbt().getCompound(LAST_RANDOM_ELEMENT_KEY).getInt(RANDOM_COST_KEY);
        return Math.max((int) sum + randomCost, 1);
    }

    public Set<ElementEntry> getNewElements() {
        Set<ElementEntry> entries = new HashSet<>(this.result.getStack(0).getStoredElementsIfBook());
        for (ElementEntry instance1 : getStack().getStoredElementsIfBook()) {
            entries.removeIf(entry -> entry.element().isOf(instance1.element()));
        }
        return entries;
    }

    public ItemStack getStack() {
        return this.input.getStack(0);
    }

    public ItemStack getMaterial() {
        return this.input.getStack(1);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id != 0) {
            return false;
        }
        if (!getMaterial().isOf(Items.DIAMOND) && !player.isCreative()) {
            return false;
        }
        getStack().getOrCreateNbt().remove(LAST_RANDOM_ELEMENT_KEY);
        if (getMaterial().isOf(Items.DIAMOND)) {
            getMaterial().decrement(1);
        }
        if (!this.player.isCreative()) {
            this.player.experienceLevel--;
        }
        this.updateResult();
        this.updateToClient();
        return true;
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        if (inventory == this.input) {
            this.updateResult();
        }
    }

    private void updateResult() {
        if (!player.world.isClient) {
            ItemStack stack = getStack();
            ItemStack material = getMaterial();
            boolean hasStack = !stack.isEmpty();
            if (hasStack) {
                ItemStack newStack = stack.copy();
                Set<ElementEntry> entries = material.getStoredElementsIfBook();
                if ((material.isEmpty() || entries.isEmpty()) && !stack.isOf(EWItems.ELEMENT_BOOK)) {
                    if (stack.getOrCreateNbt().contains(LAST_RANDOM_ELEMENT_KEY)) {
                        NbtCompound nbt = stack.getOrCreateNbt().getCompound(LAST_RANDOM_ELEMENT_KEY);
                        ElementEntry.fromNbt(nbt).ifPresent(newStack::addElement);
                    } else {
                        newStack.addNewRandomElement();
                        recordNewElement(newStack.getElements());
                    }
                } else {
                    entries.forEach(newStack::addStoredElementIfBook);
                }
                newStack.removeInvalidElements();
                Set<ElementEntry> newInstances = newStack.getStoredElementsIfBook();
                Set<ElementEntry> stackInstances = stack.getStoredElementsIfBook();
                newInstances.removeAll(stackInstances);
                if (newInstances.isEmpty() || !material.isOf(stack.getItem()) && !material.isOf(Items.DIAMOND) && !material.isOf(EWItems.ELEMENT_BOOK) && !material.isEmpty()) {
                    this.result.setStack(0, ItemStack.EMPTY);
                } else {
                    this.result.setStack(0, newStack);
                }
            } else {
                this.result.setStack(0, ItemStack.EMPTY);
            }
        }
        this.updateToClient();
        this.sendContentUpdates();
    }

    public void recordNewElement(Set<ElementEntry> entries) {
        for (ElementEntry entry : getStack().getStoredElementsIfBook()) {
            entries.remove(entry);
        }
        ElementEntry entry;
        entry = entries.size() == 1 ? entries.iterator().next() : ElementEntry.EMPTY;
        NbtCompound nbt = entry.toNbt();
        int between = entry.element().rareLevel * 3;
        nbt.putInt(RANDOM_COST_KEY, Random.create().nextBetween(-between, between));
        getStack().getOrCreateNbt().put(LAST_RANDOM_ELEMENT_KEY, nbt);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> this.dropInventory(player, this.input));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return ElementSmithingScreenHandler.canUse(this.context, player, EWBlocks.ELEMENT_SMITHING_TABLE);
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            ItemStack itemStack3 = getStack();
            ItemStack itemStack4 = getMaterial();
            if (slot == 2) {
                if (!this.insertItem(itemStack2, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot2.onQuickTransfer(itemStack2, itemStack);
            } else if (slot == 0 || slot == 1 ? !this.insertItem(itemStack2, 3, 39, false) : (itemStack3.isEmpty() || itemStack4.isEmpty() ? !this.insertItem(itemStack2, 0, 2, false) : (slot >= 3 && slot < 30 ? !this.insertItem(itemStack2, 30, 39, false) : slot >= 30 && slot < 39 && !this.insertItem(itemStack2, 3, 30, false)))) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot2.setStackNoCallbacks(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot2.onTakeItem(player, itemStack2);
        }
        return itemStack;
    }
}
