package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.init.EWItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin extends LivingEntityMixin {

    @Redirect(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
    public ActionResult interact(Entity entry, PlayerEntity player, Hand hand) {
        return player.getStackInHand(hand).isOf(EWItems.ELEMENT_DETECTOR) ? ActionResult.PASS : entry.interact(player, hand);
    }
}
