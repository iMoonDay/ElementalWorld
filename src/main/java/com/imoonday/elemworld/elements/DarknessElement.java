package com.imoonday.elemworld.elements;

import com.imoonday.elemworld.api.Element;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Map;

import static com.imoonday.elemworld.init.EWElements.LIGHT;
import static net.minecraft.entity.damage.DamageTypes.*;
import static net.minecraft.entity.effect.StatusEffects.SPEED;

public class DarknessElement extends Element {
    public DarknessElement(int maxLevel, int rareLevel, int weight) {
        super(maxLevel, rareLevel, weight);
    }

    @Override
    public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
        Float x = getMultiplier(world, entity);
        if (x != null) return x;
        return super.getMiningSpeedMultiplier(world, entity, state);
    }

    @Override
    public float getDamageMultiplier(World world, LivingEntity entity, LivingEntity target) {
        Float x = getMultiplier(world, entity);
        if (x != null) return x;
        return super.getDamageMultiplier(world, entity, target);
    }

    @Override
    public float getMaxHealthMultiplier(World world, LivingEntity entity) {
        Float x = getMultiplier(world, entity);
        if (x != null) return x / 5.0f;
        return super.getMaxHealthMultiplier(world, entity);
    }

    @Nullable
    private Float getMultiplier(World world, LivingEntity entity) {
        long time = world.getTimeOfDay();
        if (time < 1000 || time >= 13000) {
            if (world.getLightLevel(entity.getBlockPos()) == 0) {
                return 2.0f;
            }
            return 1.0f;
        }
        return null;
    }

    @Override
    public float getDamageProtectionMultiplier(DamageSource source, LivingEntity entity) {
        if (source.isIndirect()) {
            return 1.0f;
        }
        if (source.getAttacker() instanceof LivingEntity attacker) {
            long time = entity.world.getTimeOfDay();
            if (attacker.getMainHandStack().hasElement(LIGHT) || attacker.isIn(LIGHT) || time >= 1000 && time < 13000) {
                return 1.0f;
            }
        }
        return source.isOf(MOB_ATTACK) || source.isOf(MOB_ATTACK_NO_AGGRO) || source.isOf(PLAYER_ATTACK) ? 0.2f : 1.0f;
    }

    @Override
    public Map<StatusEffect, Integer> getPersistentEffects(Map<StatusEffect, Integer> effects) {
        effects.put(SPEED, 0);
        return effects;
    }

    @Override
    public Color getColor() {
        return Color.BLACK;
    }
}
