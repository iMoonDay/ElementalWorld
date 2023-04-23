package com.imoonday.elemworld.mixin;

import com.imoonday.elemworld.api.EWStatusEffectInstance;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectInstance.class)
public class StatusEffectInstanceMixin implements EWStatusEffectInstance {

    private static final String FROM_ELEMENT_KEY = "FromElement";
    private boolean fromElement = false;

    @Override
    public StatusEffectInstance setFromElement(boolean fromElement) {
        StatusEffectInstance instance = (StatusEffectInstance) (Object) this;
        this.fromElement = fromElement;
        return instance;
    }

    @Override
    public boolean isFromElement() {
        return fromElement;
    }

    @Inject(method = "writeTypelessNbt", at = @At("HEAD"))
    public void writeTypelessNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean(FROM_ELEMENT_KEY, this.fromElement);
    }

    @Inject(method = "fromNbt(Lnet/minecraft/entity/effect/StatusEffect;Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/entity/effect/StatusEffectInstance;", at = @At("RETURN"), cancellable = true)
    private static void fromNbt(StatusEffect type, NbtCompound nbt, CallbackInfoReturnable<StatusEffectInstance> cir) {
        cir.setReturnValue(cir.getReturnValue().setFromElement(nbt.getBoolean(FROM_ELEMENT_KEY)));
    }
}
