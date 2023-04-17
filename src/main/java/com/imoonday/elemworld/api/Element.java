package com.imoonday.elemworld.api;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.IntFunction;

import static net.minecraft.entity.damage.DamageTypes.*;

public enum Element implements StringIdentifiable {

    INVALID(0, "invalid", 0),
    GOLD(1, "gold", 1, 1.25f, 1.25f, 1.0f, 0.75f),
    WOOD(2, "wood", 1, 0.75f, 0.75f, 1.0f, 1.25f),
    WATER(3, "water", 1, 1.0f, 0.5f, 1.0f, 1.0f),
    FIRE(4, "fire", 1, 1.0f, 1.5f, 1.0f, 1.0f),
    EARTH(5, "earth", 1, 1.0f, 1.0f, 1.0f, 1.5f),
    WIND(6, "wind", 2, FALL),
    THUNDER(7, "thunder", 2, 1.0f, 1.75f, 1.0f, 1.0f, LIGHTNING_BOLT),
    ROCK(8, "rock", 2, 1.0f, 1.0f, 1.5f, 2.0f, IN_WALL),
    GRASS(9, "grass", 2),
    ICE(10, "ice", 2, 1.0f, 1.0f, 1.0f, 0.75f),
    LIGHT(11, "light", 3, 1.5f, 1.5f, 1.5f, 1.0f, IN_FIRE, ON_FIRE, EXPLOSION, PLAYER_EXPLOSION),
    DARKNESS(12, "darkness", 3, 2.0f, 2.0f, 2.0f, 1.0f, MOB_ATTACK, PLAYER_ATTACK),
    TIME(13, "time", 3, 1.75f, 1.75f, 1.75f, 1.0f),
    SPACE(14, "space", 3, 1.5f, 1.5f, 1.5f, 1.0f, MOB_PROJECTILE, ARROW, TRIDENT, THROWN, DROWN, EXPLOSION, PLAYER_EXPLOSION),
    SOUND(15, "sound", 3, 1.75f, 1.75f, 1.75f, 1.0f, SONIC_BOOM);

    private static final IntFunction<Element> BY_ID;
    public static final StringIdentifiable.Codec<Element> CODEC;
    private final int id;
    private final String name;
    private final int level;
    private final float miningSpeedMultiplier;
    private final float damageMultiplier;
    private final float protectionMultiplier;
    private final float durabilityMultiplier;
    private final RegistryKey<DamageType>[] ignoreDamageTypes;

    @SafeVarargs
    Element(int id, String name, int level, RegistryKey<DamageType>... ignoreDamageTypes) {
        this(id, name, level, 1.0f, 1.0f, 1.0f, 1.0f, ignoreDamageTypes);
    }

    @SafeVarargs
    Element(int id, String name, int level, float miningSpeedMultiplier, float damageMultiplier, float protectionMultiplier, float durabilityMultiplier, RegistryKey<DamageType>... ignoreDamageTypes) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.miningSpeedMultiplier = miningSpeedMultiplier;
        this.damageMultiplier = damageMultiplier;
        this.protectionMultiplier = protectionMultiplier;
        this.durabilityMultiplier = durabilityMultiplier;
        this.ignoreDamageTypes = ignoreDamageTypes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
        long time = world.getTimeOfDay();
        switch (this.id) {
            case 2 -> {
                return state.isIn(BlockTags.LOGS) ? 1.5f : miningSpeedMultiplier;
            }
            case 8 -> {
                return state.isIn(BlockTags.NEEDS_STONE_TOOL) ? 2.0f : miningSpeedMultiplier;
            }
            case 11 -> {
                return time >= 1000 && time < 13000 ? miningSpeedMultiplier : 1.0f;
            }
            case 12 -> {
                return time < 1000 || time >= 13000 ? (world.getLightLevel(entity.getBlockPos()) == 0 ? 3.0f : miningSpeedMultiplier) : 1.0f;
            }
            case 15 -> {
                return entity.isSubmergedInWater() ? 1.5f : miningSpeedMultiplier;
            }
        }
        return miningSpeedMultiplier;
    }

    public float getDamageMultiplier(World world, LivingEntity entity) {
        long time = world.getTimeOfDay();
        switch (this.id) {
            case 11 -> {
                return time >= 1000 && time < 13000 ? damageMultiplier : 1.0f;
            }
            case 12 -> {
                return time < 1000 || time >= 13000 ? (world.getLightLevel(entity.getBlockPos()) == 0 ? 3.0f : damageMultiplier) : 1.0f;
            }
            case 15 -> {
                return entity.isSubmergedInWater() ? 1.5f : damageMultiplier;
            }
        }
        return damageMultiplier;
    }

    public float getProtectionMultiplier(World world, LivingEntity entity) {
        long time = world.getTimeOfDay();
        switch (this.id) {
            case 11 -> {
                return time >= 1000 && time < 13000 ? protectionMultiplier : 1.0f;
            }
            case 12 -> {
                return time < 1000 || time >= 13000 ? (world.getLightLevel(entity.getBlockPos()) == 0 ? 3.0f : protectionMultiplier) : 1.0f;
            }
            case 15 -> {
                return entity.isSubmergedInWater() ? 1.5f : protectionMultiplier;
            }
        }
        return protectionMultiplier;
    }

    public float getDurabilityMultiplier() {
        return durabilityMultiplier;
    }

    public RegistryKey<DamageType>[] getIgnoreDamageTypes() {
        return ignoreDamageTypes;
    }

    public static Element byId(int id) {
        return BY_ID.apply(id);
    }

    public static Element createRandom() {
        Element element;
        do {
            element = Element.byId(Random.create().nextBetween(0, Element.values().length));
        } while (Random.create().nextFloat() > (float) 1 / element.level);
        return element;
    }

    public static Element createRandom(int level) {
        level = MathHelper.clamp(level, 0, 3);
        Element element = createRandom();
        while (element.level != level) {
            element = createRandom();
        }
        return element;
    }

    @Nullable
    public static MutableText getElementsText(ArrayList<Element> elements) {
        MutableText text = Text.empty();
        for (Iterator<Element> iterator = elements.iterator(); iterator.hasNext(); ) {
            Element element = iterator.next();
            Formatting color = switch (element.getLevel()) {
                case 1 -> Formatting.WHITE;
                case 2 -> Formatting.BLUE;
                case 3 -> Formatting.GOLD;
                default -> null;
            };
            if (color != null) {
                text.append(iterator.hasNext() ? Text.translatable("element.elemworld." + element.getName()).append(" ").formatted(color) : Text.translatable("element.elemworld." + element.getName()).formatted(color));
            }
        }
        if (text.equals(Text.empty())) {
            return null;
        }
        return text;
    }

    public Text getTranslationName() {
        Formatting color = switch (this.getLevel()) {
            case 1 -> Formatting.WHITE;
            case 2 -> Formatting.AQUA;
            case 3 -> Formatting.GOLD;
            default -> Formatting.GRAY;
        };
        return Text.translatable("element.elemworld." + this.getName()).formatted(color);
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
        CODEC = StringIdentifiable.createCodec(Element::values);
        BY_ID = ValueLists.createIdToValueFunction(Element::getId, Element.values(), ValueLists.OutOfBoundsHandling.ZERO);
    }

    @Override
    public String asString() {
        return this.name;
    }
}
