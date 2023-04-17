package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.api.Element;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    public void getBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
        PlayerInventory inventory = (PlayerInventory) (Object) this;
        PlayerEntity player = inventory.player;
        List<Float> list = new ArrayList<>();
        ArrayList<Element> elements = inventory.main.get(inventory.selectedSlot).getElements();
        for (Element element : elements) {
            float miningSpeedMultiplier = element.getMiningSpeedMultiplier(player.world, player, block);
            list.add(miningSpeedMultiplier);
        }
        if (!list.isEmpty()) {
            float speed = cir.getReturnValueF() * Collections.max(list);
            cir.setReturnValue(speed);
        }
    }
}
