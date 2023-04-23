package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.api.Element;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static com.imoonday.elemworld.init.EWElements.*;

@Mixin(EnderPearlEntity.class)
public class EnderPearlEntityMixin {

    @Inject(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void damage(HitResult hitResult, CallbackInfo ci, Entity entity) {
        EnderPearlEntity pearl = (EnderPearlEntity) (Object) this;
        if (entity instanceof ServerPlayerEntity player) {
            for (ItemStack armorItem : player.getArmorItems()) {
                for (Element element : armorItem.getElements()) {
                    if (element == SPACE) {
                        pearl.discard();
                        ci.cancel();
                    }
                }
            }
        }
    }
}
