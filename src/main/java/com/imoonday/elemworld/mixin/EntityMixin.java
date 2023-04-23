package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.init.EWEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "canMoveVoluntarily", at = @At("RETURN"), cancellable = true)
    public void canMoveVoluntarily(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity living) {
            if (living.hasStatusEffect(EWEffects.FREEZE)) {
                cir.setReturnValue(false);
            }
        }
    }
}
