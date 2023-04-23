package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.init.EWEffects;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "canMoveVoluntarily", at = @At("RETURN"), cancellable = true)
    public void canMoveVoluntarily(CallbackInfoReturnable<Boolean> cir) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        if (player.hasStatusEffect(EWEffects.FREEZE)) {
            cir.setReturnValue(false);
        }
    }
}
