package com.imoonday.elemworld.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class UmbrellaItem extends SwordItem implements Equipment {

    public UmbrellaItem() {
        super(ToolMaterials.WOOD, 3, -2.4f, new FabricItemSettings());
    }

    @Override
    public EquipmentSlot getSlotType() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPYGLASS;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof LivingEntity living)) {
            return;
        }
        boolean isUsing = living.isUsingItem() && living.getActiveItem().isOf(this);
        boolean shouldUse = living.getEquippedStack(EquipmentSlot.HEAD).isOf(this) && shouldUse(living);
        if (isUsing || shouldUse) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 2, 0, false, false, false));
        }
    }

    public boolean shouldUse(LivingEntity entity) {
        return !entity.isOnGround() && !entity.isTouchingWater() && !entity.isFallFlying() && !entity.isSneaking() && (!(entity instanceof PlayerEntity player) || !player.getAbilities().flying);
    }
}
