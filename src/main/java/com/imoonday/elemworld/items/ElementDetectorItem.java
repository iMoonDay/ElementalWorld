package com.imoonday.elemworld.items;

import com.imoonday.elemworld.api.EWLivingEntity;
import com.imoonday.elemworld.screens.handler.ElementDetailsScreenHandler;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
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
        user.openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return entity.getName();
            }

            @Override
            public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
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
                equipments.setStack(slot++, entity.getEquippedStack(EquipmentSlot.HEAD));
                equipments.setStack(slot++, entity.getEquippedStack(EquipmentSlot.CHEST));
                equipments.setStack(slot++, entity.getEquippedStack(EquipmentSlot.LEGS));
                equipments.setStack(slot++, entity.getEquippedStack(EquipmentSlot.FEET));
                equipments.setStack(slot++, entity.getEquippedStack(EquipmentSlot.MAINHAND));
                equipments.setStack(slot, entity.getEquippedStack(EquipmentSlot.OFFHAND));
                return new ElementDetailsScreenHandler(syncId, playerInventory, equipments);
            }
        });
    }
}
