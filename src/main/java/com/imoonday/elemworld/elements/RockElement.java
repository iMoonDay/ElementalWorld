package com.imoonday.elemworld.elements;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Map;

import static net.minecraft.entity.damage.DamageTypes.IN_WALL;

public class RockElement extends Element {
    public RockElement() {
        super(2, 2, 20, 0.0f, 0.0f, 0.5f, 1.0f);
    }

    @Override
    public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
        if (state.isIn(BlockTags.PICKAXE_MINEABLE)) {
            return 1.0f;
        }
        return super.getMiningSpeedMultiplier(world, entity, state);
    }

    @Override
    public float getDamageProtectionMultiplier(DamageSource source, LivingEntity entity) {
        return source.isOf(IN_WALL) ? 0.2f : 1.0f;
    }

    @Override
    public Color getColor() {
        return Color.GRAY;
    }

    @Override
    public Map<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(Map<EntityAttribute, EntityAttributeModifier> map, int slot) {
        if (slot != 4 && slot != 5) {
            map.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(this.getUuid(slot), this::getTranslationKey, 2, EntityAttributeModifier.Operation.ADDITION));
        }
        return map;
    }
}
