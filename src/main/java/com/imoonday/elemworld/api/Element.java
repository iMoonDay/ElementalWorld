package com.imoonday.elemworld.api;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;

import static com.imoonday.elemworld.ElementalWorld.id;
import static com.imoonday.elemworld.init.EWElements.ELEMENTS;
import static com.imoonday.elemworld.init.EWElements.EMPTY;

@SuppressWarnings("unused")
public class Element {

    public static final int LAST_INDEX = ELEMENTS.size() - 1;
    public static final Map<Integer, Integer> LEVEL_SIZE = new HashMap<>();
    public static final Map<Predicate<LivingEntity>, Float> DAMAGE_MULTIPLIER = new HashMap<>();
    private String name = "null";
    private final int level;
    private final float miningSpeedMultiplier;
    private final float damageMultiplier;
    private final float protectionMultiplier;
    private final float durabilityMultiplier;

    public Element(int level) {
        this(level, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public Element(int level, float miningSpeedMultiplier, float damageMultiplier, float protectionMultiplier, float durabilityMultiplier) {
        this.level = level;
        this.miningSpeedMultiplier = miningSpeedMultiplier;
        this.damageMultiplier = damageMultiplier;
        this.protectionMultiplier = protectionMultiplier;
        this.durabilityMultiplier = durabilityMultiplier;
    }

    public Element withName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
        return miningSpeedMultiplier;
    }

    public float getDamageMultiplier(World world, LivingEntity entity, LivingEntity target) {
        return damageMultiplier;
    }

    public float getProtectionMultiplier(World world, LivingEntity entity) {
        return protectionMultiplier;
    }

    public float getDurabilityMultiplier() {
        return durabilityMultiplier;
    }

    public boolean ignoreDamage(DamageSource source, LivingEntity entity) {
        return false;
    }

    public Map<StatusEffect, Integer> getPersistentEffects() {
        return new HashMap<>();
    }

    public UUID getUuid(int slot) {
        return UUID.nameUUIDFromBytes((this.getName() + " " + slot).getBytes(StandardCharsets.UTF_8));
    }

    public static Element byName(String name) {
        return Optional.ofNullable(ELEMENTS.get(name)).orElse(EMPTY);
    }

    public static Element createRandom() {
        Element element;
        Random random = Random.create();
        do {
            element = ELEMENTS.values().stream().toList().get(random.nextBetween(0, LAST_INDEX));
        } while (element == null || element.level == 0 || random.nextFloat() > (float) 1 / element.level);
        return element;
    }

    public static Element createRandom(int level) {
        if (ELEMENTS.values().stream().noneMatch(element -> element.level == level)) {
            return EMPTY;
        }
        Element element;
        Random random = Random.create();
        do {
            List<Element> list = ELEMENTS.values().stream().filter(element1 -> element1.level == level).toList();
            element = list.get(random.nextBetween(0, list.size() - 1));
        } while (element == null || level != 0 && random.nextFloat() > (float) 1 / element.level);
        return element;
    }

    public static Element createRandom(int level, ArrayList<Element> exclude) {
        int maxSize = LEVEL_SIZE.get(level);
        List<Element> elements = exclude.stream().filter(element1 -> element1.level == level).toList();
        if (elements.size() >= maxSize) {
            return EMPTY;
        }
        Element element;
        do {
            element = createRandom(level);
        } while (element.isOneOf(exclude));
        return element;
    }

    @Nullable
    public static Text getElementsText(ArrayList<Element> elements, boolean prefix) {
        if (elements.size() == 0) {
            return null;
        }
        if (elements.size() == 1 && elements.get(0) == EMPTY) {
            return null;
        }
        MutableText text = prefix ? Text.literal("[元素]").formatted(Formatting.WHITE) : Text.empty();
        elements.sort(Comparator.comparingInt(element -> element.level));
        for (Element element : elements) {
            Text translationName = element.getTranslationName();
            if (translationName != null) {
                if (!text.equals(Text.empty())) {
                    text.append(" ");
                }
                text.append(translationName);
            }
        }
        return text;
    }

    @Nullable
    public Text getTranslationName() {
        Formatting color = switch (this.getLevel()) {
            case 1 -> Formatting.WHITE;
            case 2 -> Formatting.AQUA;
            case 3 -> Formatting.GOLD;
            default -> null;
        };
        return color == null ? null : Text.translatable(this.getTranslationKey()).formatted(color);
    }

    public String getTranslationKey() {
        return "element.elemworld." + this.getName();
    }

    public Color getColor() {
        return switch (this.getLevel()) {
            case 1 -> Color.WHITE;
            case 2 -> Color.CYAN;
            case 3 -> Color.ORANGE;
            default -> Color.GRAY;
        };
    }

    static {
        for (Element element : ELEMENTS.values()) {
            int level = element.level;
            LEVEL_SIZE.put(level, Optional.ofNullable(LEVEL_SIZE.get(level)).orElse(0) + 1);
            element.writeDamageMultiplier(DAMAGE_MULTIPLIER);
        }
    }

    public StatusEffect getEffect() {
        return Effect.get(this);
    }

    public void addEffect(LivingEntity target, @Nullable PlayerEntity attacker) {
        int sec = this.getEffectTime(target);
        if (sec > 0) {
            StatusEffectInstance effect = new StatusEffectInstance(this.getEffect(), sec * 20, 0);
            target.addStatusEffect(effect, attacker);
            if (!target.world.isClient) {
                ArrayList<ServerPlayerEntity> entities = new ArrayList<>(PlayerLookup.tracking(target));
                if (target instanceof ServerPlayerEntity player) {
                    entities.add(player);
                }
                for (ServerPlayerEntity player : entities) {
                    player.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(player.getId(), effect));
                }
            }
        }
    }

    public void addPersistentEffects(LivingEntity entity) {
        for (Map.Entry<StatusEffect, Integer> entry : this.getPersistentEffects().entrySet()) {
            StatusEffect effect = entry.getKey();
            int amplifier = entry.getValue();
            StatusEffectInstance instance = entity.getStatusEffect(effect);
            if (instance == null || instance.isFromElement() && instance.getAmplifier() != amplifier) {
                entity.addStatusEffect(new StatusEffectInstance(effect, -1, amplifier, false, false, false).setFromElement(true));
            }
        }
    }

    public int getEffectTime(LivingEntity target) {
        return 5;
    }

    public boolean isOneOf(Element... elements) {
        return Arrays.stream(elements).anyMatch(element -> this == element);
    }

    public boolean isOneOf(ArrayList<Element> elements) {
        return elements.contains(this);
    }

    public static void register() {
        for (Element element : ELEMENTS.values()) {
            if (element == EMPTY) {
                continue;
            }
            Registry.register(Registries.STATUS_EFFECT, id(element.getName()), new Effect(element));
        }
    }

    public boolean shouldImmuneOnDeath(LivingEntity entity) {
        return false;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        //元素效果
    }

    public void tick(LivingEntity entity) {

    }

    public void postHit(LivingEntity target, PlayerEntity attacker) {
        //物品攻击后
    }

    public void postHit(LivingEntity target, LivingEntity attacker, float amount) {
        //物品攻击后，带伤害
    }

    public void afterInjury(LivingEntity entity, DamageSource source, float amount) {
        //受伤后，带伤害
    }

    public void writeDamageMultiplier(Map<Predicate<LivingEntity>, Float> map) {

    }

    public float getExtraDamage(LivingEntity target, float amount) {
        return 0.0f;
    }

    public void onEffectApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {

    }

    public void onEffectRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {

    }

    public boolean shouldAddEffect(LivingEntity entity) {
        return false;
    }

    public boolean shouldAddEffectAfterInjury(LivingEntity entity, DamageSource source, float amount) {
        return false;
    }

    public void onElementApplied(LivingEntity entity, int slot) {
        if (entity.world.isClient) {
            return;
        }
        AttributeContainer attributes = entity.getAttributes();
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : this.getAttributeModifiers(slot).entrySet()) {
            EntityAttributeInstance entityAttributeInstance = attributes.getCustomInstance(entry.getKey());
            if (entityAttributeInstance == null) continue;
            EntityAttributeModifier entityAttributeModifier = entry.getValue();
            entityAttributeInstance.removeModifier(entityAttributeModifier);
            entityAttributeInstance.addPersistentModifier(new EntityAttributeModifier(entityAttributeModifier.getId(), this.getTranslationKey() + " " + slot, entityAttributeModifier.getValue(), entityAttributeModifier.getOperation()));
        }
    }

    public void onElementRemoved(LivingEntity entity, int slot) {
        if (entity.world.isClient) {
            return;
        }
        AttributeContainer attributes = entity.getAttributes();
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : this.getAttributeModifiers(slot).entrySet()) {
            EntityAttributeInstance entityAttributeInstance = attributes.getCustomInstance(entry.getKey());
            if (entityAttributeInstance == null) continue;
            entityAttributeInstance.removeModifier(entry.getValue());
            if (entity.getHealth() > entity.getMaxHealth()) {
                entity.setHealth(entity.getMaxHealth());
            }
        }
    }

    public Map<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(int slot) {
        return new HashMap<>();
    }

    private static class Effect extends StatusEffect {

        private final Element element;

        private Effect(Element element) {
            super(StatusEffectCategory.HARMFUL, element.getColor().getRGB());
            this.element = element;
        }

        @Override
        public boolean canApplyUpdateEffect(int duration, int amplifier) {
            return true;
        }

        @Override
        public void applyUpdateEffect(LivingEntity entity, int amplifier) {
            this.element.applyUpdateEffect(entity, amplifier);
        }

        @Override
        public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
            super.onApplied(entity, attributes, amplifier);
            this.element.onEffectApplied(entity, attributes, amplifier);
        }

        @Override
        public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
            super.onRemoved(entity, attributes, amplifier);
            this.element.onEffectRemoved(entity, attributes, amplifier);
        }

        public static StatusEffect get(Element element) {
            return Registries.STATUS_EFFECT.get(id(element.name));
        }

        @Override
        public Text getName() {
            return this.element.getTranslationName();
        }
    }
}
