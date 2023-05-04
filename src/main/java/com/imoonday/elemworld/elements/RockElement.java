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

import java.util.Map;

import static net.minecraft.entity.damage.DamageTypes.IN_WALL;

public class RockElement extends Element {
    public RockElement(int maxLevel, int rareLevel, int weight, float miningSpeedMultiplier, float damageMultiplier, float maxHealthMultiplier, float durabilityMultiplier) {
        super(maxLevel, rareLevel, weight, miningSpeedMultiplier, damageMultiplier, maxHealthMultiplier, durabilityMultiplier);
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
    public void getAttributeModifiers(Map<EntityAttribute, EntityAttributeModifier> map, int slot) {
        map.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(this.getUuid(slot), this::getTranslationKey, 2, EntityAttributeModifier.Operation.ADDITION));
    }
}
