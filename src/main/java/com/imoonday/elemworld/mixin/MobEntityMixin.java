package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.init.EWEffects;
import com.imoonday.elemworld.init.EWElements;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public class MobEntityMixin {

    @Inject(method = "canMoveVoluntarily", at = @At("RETURN"), cancellable = true)
    public void canMoveVoluntarily(CallbackInfoReturnable<Boolean> cir) {
        MobEntity entity = (MobEntity) (Object) this;
        if (entity.hasStatusEffect(EWEffects.FREEZE)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isAffectedByDaylight", at = @At("HEAD"), cancellable = true)
    public void isAffectedByDaylight(CallbackInfoReturnable<Boolean> cir) {
        MobEntity entity = (MobEntity) (Object) this;
        if (entity.hasElement(EWElements.LIGHT)) {
            cir.setReturnValue(false);
        }
    }
}
