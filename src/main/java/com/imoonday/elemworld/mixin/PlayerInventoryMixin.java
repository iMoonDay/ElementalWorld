package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.api.Element;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Set;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    public void getBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
        PlayerInventory inventory = (PlayerInventory) (Object) this;
        PlayerEntity player = inventory.player;
        Set<Map.Entry<Element, Integer>> elements = inventory.main.get(inventory.selectedSlot).getElements().entrySet();
        float multiplier = 1.0f;
        for (Map.Entry<Element, Integer> map : elements) {
            Element element = map.getKey();
            if (element == null || element.isInvalid()) {
                continue;
            }
            float f = element.getMiningSpeedMultiplier(player.world, player, block);
            multiplier += element.getLevelMultiplier(map.getValue(), f);
        }
        float finalSpeed = cir.getReturnValueF() * multiplier;
        cir.setReturnValue(Float.valueOf(String.format("%.2f", finalSpeed)));
    }
}
