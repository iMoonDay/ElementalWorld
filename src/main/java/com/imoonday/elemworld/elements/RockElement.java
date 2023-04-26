package com.imoonday.elemworld.elements;

import com.imoonday.elemworld.api.Element;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.entity.damage.DamageTypes.IN_WALL;

public class RockElement extends Element {
    public RockElement(int maxLevel, int rareLevel, int weight, float miningSpeedMultiplier, float damageMultiplier, float protectionMultiplier, float durabilityMultiplier) {
        super(maxLevel, rareLevel, weight, miningSpeedMultiplier, damageMultiplier, protectionMultiplier, durabilityMultiplier);
    }

    @Override
    public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
        if (state.isIn(BlockTags.PICKAXE_MINEABLE)) {
            return 2.0f;
        }
        return super.getMiningSpeedMultiplier(world, entity, state);
    }

    @Override
    public boolean ignoreDamage(DamageSource source, LivingEntity entity) {
        return source.isOf(IN_WALL);
    }

    @Override
    public Map<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(int slot) {
        Map<EntityAttribute, EntityAttributeModifier> map = new HashMap<>();
        map.put(EntityAttributes.GENERIC_MAX_HEALTH, new EntityAttributeModifier(this.getUuid(slot), this::getTranslationKey, 4.0, EntityAttributeModifier.Operation.ADDITION));
        return map;
    }
}
