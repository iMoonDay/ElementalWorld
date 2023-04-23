package com.imoonday.elemworld.elements;

import com.imoonday.elemworld.api.Element;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static com.imoonday.elemworld.init.EWElements.LIGHT;
import static net.minecraft.entity.damage.DamageTypes.*;
import static net.minecraft.entity.effect.StatusEffects.SPEED;

public class DarknessElement extends Element {
    public DarknessElement(int level) {
        super(level);
    }

    @Override
    public float getMiningSpeedMultiplier (World world, LivingEntity entity, BlockState state){
        Float x = getMultiplier(world, entity);
        if (x != null) return x;
        return super.getMiningSpeedMultiplier(world, entity, state);
    }

    @Override
    public float getDamageMultiplier (World world, LivingEntity entity, LivingEntity target){
        Float x = getMultiplier(world, entity);
        if (x != null) return x;
        return super.getDamageMultiplier(world, entity, target);
    }

    @Override
    public float getProtectionMultiplier (World world, LivingEntity entity){
        Float x = getMultiplier(world, entity);
        if (x != null) return x;
        return super.getProtectionMultiplier(world, entity);
    }

    @Nullable
    private Float getMultiplier (World world, LivingEntity entity){
        long time = world.getTimeOfDay();
        if (time < 1000 || time >= 13000) {
            if (world.getLightLevel(entity.getBlockPos()) == 0) {
                return 3.0f;
            }
            return 2.0f;
        }
        return null;
    }

    @Override
    public boolean ignoreDamage (DamageSource source, LivingEntity entity){
        if (source.isIndirect()) {
            return false;
        }
        if (source.getAttacker() instanceof LivingEntity attacker) {
            long time = entity.world.getTimeOfDay();
            if (attacker.getMainHandStack().hasElement(LIGHT) || attacker.isIn(LIGHT) || time >= 1000 && time < 13000) {
                return false;
            }
        }
        return source.isOf(MOB_ATTACK) || source.isOf(MOB_ATTACK_NO_AGGRO) || source.isOf(PLAYER_ATTACK);
    }

    @Override
    public Map<StatusEffect, Integer> getPersistentEffects () {
        Map<StatusEffect, Integer> effects = new HashMap<>();
        effects.put(SPEED, 0);
        return effects;
    }
}