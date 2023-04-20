package com.imoonday.elemworld.screens.handler;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.init.EWBlocks;
import com.imoonday.elemworld.init.EWScreens;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;

public class ModifyElementsScreenHandler extends ScreenHandler {

    public static final String LAST_RANDOM_ELEMENT_KEY = "LastRandomElement";
    private final Inventory result = new CraftingResultInventory();
    private final Inventory input = new SimpleInventory(2) {
        @Override
        public void markDirty() {
            super.markDirty();
            ModifyElementsScreenHandler.this.onContentChanged(this);
        }
    };
    private final ScreenHandlerContext context;
    private final PlayerEntity player;

    public ModifyElementsScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, ScreenHandlerContext.EMPTY);
    }

    public Inventory getInput() {
        return input;
    }

    public Inventory getResult() {
        return result;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public ModifyElementsScreenHandler(int syncId, PlayerInventory playerInventory, final ScreenHandlerContext context) {
        super(EWScreens.MODIFY_ELEMENTS_SCREEN_HANDLER, syncId);
        this.context = context;
        this.player = playerInventory.player;
        checkSize(input, 2);
        int y;
        int x;
        this.addSlot(new Slot(this.input, 0, 27, 34) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isDamageable();
            }
        });
        this.addSlot(new Slot(this.input, 1, 76, 34) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.DIAMOND) || !stack.getElements().isEmpty();
            }
        });
        this.addSlot(new Slot(this.result, 2, 134, 34) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity) {
                return playerEntity.experienceLevel >= getRequiredLevel() || playerEntity.getAbilities().creativeMode;
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                ModifyElementsScreenHandler.this.input.setStack(0, ItemStack.EMPTY);
                if (!getMaterial().isOf(Items.DIAMOND)) {
                    ModifyElementsScreenHandler.this.input.setStack(1, ItemStack.EMPTY);
                }
                stack.getOrCreateNbt().remove(LAST_RANDOM_ELEMENT_KEY);
                if (!player.getAbilities().creativeMode) {
                    player.experienceLevel -= ModifyElementsScreenHandler.this.getRequiredLevel();
                }
                ModifyElementsScreenHandler.this.input.markDirty();
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
        if (getMaterial().isEmpty() && !this.player.getAbilities().creativeMode) {
            return true;
        }
        if (!getMaterial().isEmpty() && !getMaterial().isOf(Items.DIAMOND)) {
            return true;
        }
        if (this.player.experienceLevel < 1 && !this.player.getAbilities().creativeMode) {
            return true;
        }
        if (getStack().isEmpty()) {
            return true;
        }
        return getStack().getElements().size() >= Element.MAX_SIZE;
    }

    public int getRequiredLevel() {
        ArrayList<Element> elements = this.result.getStack(0).getElements();
        return elements.stream().mapToInt(Element::getLevel).map(level -> level * elements.size()).sum();
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
        if (!getMaterial().isOf(Items.DIAMOND) && !player.getAbilities().creativeMode) {
            return false;
        }
        getStack().getOrCreateNbt().remove(LAST_RANDOM_ELEMENT_KEY);
        getMaterial().decrement(1);
        if (!this.player.getAbilities().creativeMode) {
            this.player.experienceLevel--;
        }
        this.updateResult();
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
        ItemStack stack = getStack();
        ItemStack material = getMaterial();
        boolean hasStack = !stack.isEmpty();
        if (hasStack) {
            ItemStack newStack = stack.copy();
            ArrayList<Element> elements = material.getElements();
            if (material.isEmpty() || elements.isEmpty()) {
                if (stack.getOrCreateNbt().contains(LAST_RANDOM_ELEMENT_KEY)) {
                    newStack.addElement(Element.byId(stack.getOrCreateNbt().getInt(LAST_RANDOM_ELEMENT_KEY)));
                } else {
                    newStack.addNewRandomElements(1);
                    recordNewElement(newStack.getElements());
                }
            } else {
                elements.forEach(newStack::addElement);
            }
            ArrayList<Element> list = new ArrayList<>(newStack.getElements());
            list.removeAll(stack.getElements());
            if (list.isEmpty() || !material.isOf(stack.getItem()) && !material.isOf(Items.DIAMOND) && !material.isEmpty()) {
                this.result.setStack(0, ItemStack.EMPTY);
            } else {
                this.result.setStack(0, newStack);
            }
        } else {
            this.result.setStack(0, ItemStack.EMPTY);
        }
        this.sendContentUpdates();
    }

    public void recordNewElement(ArrayList<Element> elements) {
        elements.removeAll(getStack().getElements());
        Element element = elements.isEmpty() ? Element.INVALID : elements.get(0);
        getStack().getOrCreateNbt().putInt(LAST_RANDOM_ELEMENT_KEY, element.getId());
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> this.dropInventory(player, this.input));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return ModifyElementsScreenHandler.canUse(this.context, player, EWBlocks.ELEMENT_MODIFIER_BLOCK);
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
