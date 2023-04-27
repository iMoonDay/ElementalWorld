package com.imoonday.elemworld.items;

import com.imoonday.elemworld.api.Element;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ElementDetectorItem extends Item {
    public ElementDetectorItem() {
        super(new Settings().maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) {
            return TypedActionResult.success(stack);
        }
        for (Text text : Element.getElementsText(user.getElements(), false, false)) {
            user.sendMessage(text, true);
        }
        return TypedActionResult.consume(stack);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (user.world.isClient) {
            return ActionResult.SUCCESS;
        }
        for (Text text : Element.getElementsText(entity.getElements(), false, false)) {
            user.sendMessage(text, true);
        }
        return super.useOnEntity(stack, user, entity, hand);
    }
}
