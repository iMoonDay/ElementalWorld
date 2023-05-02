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
        if (!user.isSneaking()) {
            return TypedActionResult.fail(stack);
        }
        if (world.isClient) {
            return TypedActionResult.success(stack);
        }
        sendElements(user, user);
        user.getItemCooldownManager().set(this, 20);
        return TypedActionResult.consume(stack);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (user.world.isClient) {
            return ActionResult.SUCCESS;
        }
        sendElements(user, entity);
        user.getItemCooldownManager().set(this, 20);
        return ActionResult.CONSUME;
    }

    private static void sendElements(PlayerEntity user, LivingEntity entity) {
        for (Text text : Element.getElementsText(entity.getElements(), false, false)) {
            user.sendMessage(text, true);
        }
    }
}
