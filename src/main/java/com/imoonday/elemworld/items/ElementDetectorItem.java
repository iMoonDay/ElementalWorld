package com.imoonday.elemworld.items;

import com.imoonday.elemworld.screens.handler.ElementDetailsScreenHandler;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ElementDetectorItem extends Item {
    public ElementDetectorItem() {
        super(new Settings().maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!user.isSneaking()) {
            return TypedActionResult.fail(stack);
        }
        if (world.isClient) {
            return TypedActionResult.success(stack);
        }
        openScreen(user, user);
        user.getItemCooldownManager().set(this, 20);
        return TypedActionResult.consume(stack);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (user.world.isClient) {
            return ActionResult.SUCCESS;
        }
        openScreen(user, entity);
        return ActionResult.CONSUME;
    }

    private static void openScreen(PlayerEntity user, LivingEntity entity) {
        user.openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return entity.getDisplayName();
            }

            @Override
            public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                SimpleInventory equipments = new SimpleInventory(7);
                ItemStack itemStack = Items.IRON_PICKAXE.getDefaultStack();
                itemStack.setElements(entity.getElements());
                int slot = 0;
                equipments.setStack(slot++, itemStack);
                equipments.setStack(slot++, entity.getEquippedStack(EquipmentSlot.HEAD).copy());
                equipments.setStack(slot++, entity.getEquippedStack(EquipmentSlot.CHEST).copy());
                equipments.setStack(slot++, entity.getEquippedStack(EquipmentSlot.LEGS).copy());
                equipments.setStack(slot++, entity.getEquippedStack(EquipmentSlot.FEET).copy());
                equipments.setStack(slot++, entity.getMainHandStack().copy());
                equipments.setStack(slot, entity.getOffHandStack().copy());
                return new ElementDetailsScreenHandler(syncId, playerInventory, equipments);
            }
        });
    }
}
