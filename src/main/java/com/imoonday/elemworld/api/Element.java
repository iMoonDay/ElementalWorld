package com.imoonday.elemworld.api;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.IntFunction;

import static com.imoonday.elemworld.ElementalWorld.id;
import static net.minecraft.entity.damage.DamageTypes.*;

public enum Element implements StringIdentifiable {

    INVALID(0, "invalid", 0),
    GOLD(1, "gold", 1, 1.25f, 1.25f, 1.0f, 0.75f),
    WOOD(2, "wood", 1, 0.75f, 0.75f, 1.0f, 1.25f) {
        @Override
        public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
            if (state.isIn(BlockTags.LOGS)) {
                return 1.5f;
            }
            return super.getMiningSpeedMultiplier(world, entity, state);
        }
    },
    WATER(3, "water", 1, 1.0f, 0.5f, 1.0f, 1.0f),
    FIRE(4, "fire", 1, 1.0f, 1.5f, 1.0f, 1.0f){
        @Override
        public float getDamageMultiplier(World world, LivingEntity entity, LivingEntity target) {
            if (target.isInElement(WATER)) {
                return 0.75f;
            }
            return super.getDamageMultiplier(world, entity, target);
        }
    },
    EARTH(5, "earth", 1, 1.0f, 1.0f, 1.0f, 1.5f),
    WIND(6, "wind", 2, FALL),
    THUNDER(7, "thunder", 2, 1.0f, 1.75f, 1.0f, 1.0f, LIGHTNING_BOLT),
    ROCK(8, "rock", 2, 1.0f, 1.0f, 1.5f, 2.0f, IN_WALL) {
        @Override
        public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
            if (state.isIn(BlockTags.NEEDS_STONE_TOOL)) {
                return 2.0f;
            }
            return super.getMiningSpeedMultiplier(world, entity, state);
        }
    },
    GRASS(9, "grass", 2),
    ICE(10, "ice", 2, 1.0f, 1.0f, 1.0f, 0.75f),
    LIGHT(11, "light", 3, IN_FIRE, ON_FIRE, EXPLOSION, PLAYER_EXPLOSION) {
        @Override
        public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
            long time = world.getTimeOfDay();
            if (time >= 1000 && time < 13000) {
                return 1.5f;
            }
            return super.getMiningSpeedMultiplier(world, entity, state);
        }

        @Override
        public float getDamageMultiplier(World world, LivingEntity entity, LivingEntity target) {
            long time = world.getTimeOfDay();
            if (time >= 1000 && time < 13000) {
                return 1.5f;
            }
            return super.getDamageMultiplier(world, entity, target);
        }

        @Override
        public float getProtectionMultiplier(World world, LivingEntity entity) {
            long time = world.getTimeOfDay();
            if (time >= 1000 && time < 13000) {
                return 1.5f;
            }
            return super.getProtectionMultiplier(world, entity);
        }
    },
    DARKNESS(12, "darkness", 3, MOB_ATTACK, PLAYER_ATTACK) {
        @Override
        public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
            long time = world.getTimeOfDay();
            if (time < 1000 || time >= 13000) {
                if (world.getLightLevel(entity.getBlockPos()) == 0) {
                    return 3.0f;
                }
                return 2.0f;
            }
            return super.getMiningSpeedMultiplier(world, entity, state);
        }

        @Override
        public float getDamageMultiplier(World world, LivingEntity entity, LivingEntity target) {
            long time = world.getTimeOfDay();
            if (time < 1000 || time >= 13000) {
                if (world.getLightLevel(entity.getBlockPos()) == 0) {
                    return 3.0f;
                }
                return 2.0f;
            }
            return super.getDamageMultiplier(world, entity, target);
        }

        @Override
        public float getProtectionMultiplier(World world, LivingEntity entity) {
            long time = world.getTimeOfDay();
            if (time < 1000 || time >= 13000) {
                if (world.getLightLevel(entity.getBlockPos()) == 0) {
                    return 3.0f;
                }
                return 2.0f;
            }
            return super.getProtectionMultiplier(world, entity);
        }
    },
    TIME(13, "time", 3, 1.75f, 1.75f, 1.75f, 1.0f),
    SPACE(14, "space", 3, 1.5f, 1.5f, 1.5f, 1.0f, MOB_PROJECTILE, ARROW, TRIDENT, THROWN, DROWN, EXPLOSION, PLAYER_EXPLOSION),
    SOUND(15, "sound", 3, 1.75f, 1.75f, 1.75f, 1.0f, SONIC_BOOM) {
        @Override
        public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
            if (entity.isSubmergedInWater()) {
                return 1.5f;
            }
            return super.getMiningSpeedMultiplier(world, entity, state);
        }

        @Override
        public float getDamageMultiplier(World world, LivingEntity entity, LivingEntity target) {
            if (entity.isSubmergedInWater()) {
                return 1.5f;
            }
            return super.getDamageMultiplier(world, entity, target);
        }

        @Override
        public float getProtectionMultiplier(World world, LivingEntity entity) {
            if (entity.isSubmergedInWater()) {
                return 1.5f;
            }
            return super.getProtectionMultiplier(world, entity);
        }
    };

    private static final IntFunction<Element> BY_ID;
    public static final StringIdentifiable.Codec<Element> CODEC;
    public static final int MAX_SIZE = Element.values().length - 1;
    public static final HashMap<Integer, Integer> LEVEL_SIZE = new HashMap<>();
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

    public RegistryKey<DamageType>[] getIgnoreDamageTypes() {
        return ignoreDamageTypes;
    }

    public static Element byId(int id) {
        return BY_ID.apply(id);
    }

    public static Element createRandom() {
        Element element;
        Random random = Random.create();
        do {
            element = Element.byId(random.nextBetween(0, Element.values().length - 1));
        } while (element == null || random.nextFloat() > (float) 1 / element.level);
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

    public static Element createRandom(int level, ArrayList<Element> exclude) {
        level = MathHelper.clamp(level, 0, 3);
        Element element = createRandom();
        int maxSize = LEVEL_SIZE.get(level);
        List<Element> elements = new ArrayList<>();
        for (Element element1 : exclude) {
            if (element1.level == level) {
                elements.add(element1);
            }
        }
        if (elements.size() >= maxSize) {
            return INVALID;
        }
        while (element.level != level || element.isOneOf(exclude)) {
            element = createRandom();
        }
        return element;
    }

    @Nullable
    public static MutableText getElementsText(ArrayList<Element> elements, boolean prefix) {
        if (elements.size() == 0) {
            return null;
        }
        if (elements.size() == 1 && elements.get(0) == INVALID) {
            return null;
        }
        MutableText text = prefix ? Text.literal("[元素]").formatted(Formatting.WHITE) : Text.empty();
        elements.sort(Comparator.comparingInt(o -> o.id));
        for (Element element : elements) {
            Formatting color = switch (element.getLevel()) {
                case 1 -> Formatting.WHITE;
                case 2 -> Formatting.AQUA;
                case 3 -> Formatting.GOLD;
                default -> null;
            };
            if (color != null) {
                if (!text.equals(Text.empty())) {
                    text.append(" ");
                }
                text.append(Text.translatable("element.elemworld." + element.getName()).formatted(color));
            }
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
        return Text.translatable(this.getTranslationKey()).formatted(color);
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
        CODEC = StringIdentifiable.createCodec(Element::values);
        BY_ID = ValueLists.createIdToValueFunction(Element::getId, Element.values(), ValueLists.OutOfBoundsHandling.ZERO);
        for (Element element : Element.values()) {
            int level = element.level;
            LEVEL_SIZE.put(level, Optional.ofNullable(LEVEL_SIZE.get(level)).orElse(0) + 1);
        }
    }

    @Override
    public String asString() {
        return this.name;
    }

    public StatusEffect asEffect() {
        return getEffect(this);
    }

    public static StatusEffect getEffect(Element element) {
        return Effect.get(element);
    }

    public boolean isOneOf(Element... elements) {
        return Arrays.stream(elements).anyMatch(element -> this == element);
    }

    public boolean isOneOf(ArrayList<Element> elements) {
        return elements.contains(this);
    }

    public static void register() {
        for (Element element : Element.values()) {
            if (element == Element.INVALID) {
                continue;
            }
            Registry.register(Registries.STATUS_EFFECT, id(element.getName()), new Effect(element));
        }
    }

    private static class Effect extends StatusEffect {

        private final Element element;

        private Effect(Element element) {
            super(StatusEffectCategory.NEUTRAL, element.getColor().getRGB());
            this.element = element;
        }

        @Override
        public boolean canApplyUpdateEffect(int duration, int amplifier) {
            return true;
        }

        @Override
        public void applyUpdateEffect(LivingEntity entity, int amplifier) {
            if (this.element == Element.ICE) {
                if (entity.isInElement(WATER) || entity.isTouchingWater()) {
                    entity.setVelocity(Vec3d.ZERO);
                } else {
                    entity.setVelocity(entity.getVelocity().multiply(0.5));
                }
            }
        }

        public static StatusEffect get(Element element) {
            return Registries.STATUS_EFFECT.get(id(element.getName()));
        }

        @Override
        public Text getName() {
            return this.element.getTranslationName();
        }
    }
}
