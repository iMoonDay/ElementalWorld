package com.imoonday.elemworld.api;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.imoonday.elemworld.init.EWElements;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import static com.imoonday.elemworld.ElementalWorld.id;

@SuppressWarnings("unused")
public class Element {

    public static final String[] LEVELS = {"", "-I", "-II", "-III", "-IV", "-V", "-VI", "-VII", "-VIII", "-IX", "-X"};
    private static final ConcurrentHashMap<String, Element> ELEMENTS = new ConcurrentHashMap<>();
    public static final String NAME_KEY = "Name";
    public static final String LEVEL_KEY = "Level";
    private static volatile boolean frozen = false;
    private String name = "null";
    private int level = 0;
    protected final int maxLevel;
    protected final int rareLevel;
    protected final int weight;
    protected final float miningSpeedMultiplier;
    protected final float damageMultiplier;
    protected final float protectionMultiplier;
    protected final float durabilityMultiplier;

    public Element(int maxLevel, int rareLevel, int weight) {
        this(maxLevel, rareLevel, weight, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public Element(int maxLevel, int rareLevel, int weight, float miningSpeedMultiplier, float damageMultiplier, float protectionMultiplier, float durabilityMultiplier) {
        this.maxLevel = maxLevel;
        this.rareLevel = rareLevel;
        this.weight = weight;
        this.miningSpeedMultiplier = miningSpeedMultiplier;
        this.damageMultiplier = damageMultiplier;
        this.protectionMultiplier = protectionMultiplier;
        this.durabilityMultiplier = durabilityMultiplier;
    }

    /**
     * String -> Element name
     */
    public static @NotNull ImmutableMap<String, Element> getRegistryMap() {
        return ImmutableMap.copyOf(ELEMENTS);
    }

    public static @NotNull ImmutableSet<Element> getRegistrySet() {
        return ImmutableSet.copyOf(ELEMENTS.values());
    }

    /**
     * @param name    Element name
     * @param element Element
     */
    public static Element register(String name, Element element) {
        if (frozen) throw new IllegalStateException("Registry is already frozen");
        if (ELEMENTS.containsKey(name)) throw new IllegalStateException("The name is already registered");
        Element value = element.withName(name).withLevel(0);
        ELEMENTS.put(name, value);
        return value;
    }

    @NotNull
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString(NAME_KEY, this.getName());
        nbt.putInt(LEVEL_KEY, this.getLevel());
        return nbt;
    }

    private Element withName(String name) {
        this.name = name;
        return this;
    }

    public Element withLevel(int level) {
        this.level = Math.max(level, 0);
        return this;
    }

    public String getName() {
        return name;
    }

    public int getRareLevel() {
        return rareLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getLevel() {
        if (level == 0 && maxLevel > 0) {
            if (this.maxLevel == 1) {
                this.level = 1;
            } else {
                WeightRandom<Integer> random = WeightRandom.create();
                for (int i = 1; i <= this.maxLevel; i++) {
                    random.add(i, this.weight * (this.maxLevel - i + 1));
                }
                this.level = Optional.ofNullable(random.next()).orElse(1);
            }
        }
        return level;
    }

    public float getLevelMultiplier(float multiplier) {
        float f1 = (float) this.getLevel() / this.getMaxLevel();
        float f2 = (this.getMaxLevel() > 1) ? ((float) (this.getMaxLevel() - this.getLevel()) / (this.getMaxLevel() - 1)) : 1.0f;
        multiplier *= multiplier >= 0 ? f1 : f2;
        return multiplier;
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

    public int getWeight() {
        return weight;
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
        return Optional.ofNullable(getRegistryMap().get(name)).orElse(EWElements.EMPTY);
    }

    public static Element fromNbt(@NotNull NbtCompound nbt) {
        if (nbt.contains(NAME_KEY, NbtElement.STRING_TYPE)) {
            String name = nbt.getString(NAME_KEY);
            Element element = getRegistryMap().get(name);
            if (element == null) {
                return EWElements.EMPTY;
            }
            if (nbt.contains(LEVEL_KEY, NbtElement.INT_TYPE)) {
                int level = nbt.getInt(LEVEL_KEY);
                return element.withLevel(level);
            }
        }
        return EWElements.EMPTY;
    }

    @Override
    public String toString() {
        return "Element{" +
                "name='" + name + '\'' +
                ", level=" + level +
                ", maxLevel=" + maxLevel +
                ", rareLevel=" + rareLevel +
                ", weight=" + weight +
                ", miningSpeedMultiplier=" + miningSpeedMultiplier +
                ", damageMultiplier=" + damageMultiplier +
                ", protectionMultiplier=" + protectionMultiplier +
                ", durabilityMultiplier=" + durabilityMultiplier +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Element element)) return false;
        return maxLevel == element.maxLevel && rareLevel == element.rareLevel && weight == element.weight && Float.compare(element.miningSpeedMultiplier, miningSpeedMultiplier) == 0 && Float.compare(element.damageMultiplier, damageMultiplier) == 0 && Float.compare(element.protectionMultiplier, protectionMultiplier) == 0 && Float.compare(element.durabilityMultiplier, durabilityMultiplier) == 0 && Objects.equals(name, element.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, maxLevel, rareLevel, weight, miningSpeedMultiplier, damageMultiplier, protectionMultiplier, durabilityMultiplier);
    }

    private Element withRandomLevel() {
        if (this.maxLevel <= 0) {
            return this.withLevel(0);
        }
        if (this.maxLevel == 1) {
            return this.withLevel(1);
        }
        WeightRandom<Integer> random = WeightRandom.create();
        for (int i = 1; i <= this.maxLevel; i++) {
            random.add(i, this.weight * (this.maxLevel - i + 1));
        }
        return this.withLevel(Optional.ofNullable(random.next()).orElse(0));
    }

    private void setRandomLevel() {
        this.level = this.withRandomLevel().level;
    }

    public static Element createRandom(ItemStack stack) {
        return Optional.ofNullable(WeightRandom.getRandom(getRegistrySet(), element -> element.isSuitableFor(stack), Element::getWeight)).orElse(EWElements.EMPTY).withRandomLevel();
    }

    public static Element createRandom(ItemStack stack, ArrayList<Element> exclude) {
        return Optional.ofNullable(WeightRandom.getRandom(getRegistrySet(), element -> !element.isIn(exclude) && element.isSuitableFor(stack), Element::getWeight)).orElse(EWElements.EMPTY).withRandomLevel();
    }

    public static Element createRandom(LivingEntity entity) {
        return Optional.ofNullable(WeightRandom.getRandom(getRegistrySet(), element -> element.isSuitableFor(entity), Element::getWeight))
                .orElse(EWElements.EMPTY).withRandomLevel();
    }

    public static @NotNull List<Text> getElementsText(@NotNull ArrayList<Element> elements, boolean prefix, boolean lineBreak) {
        if (elements.size() == 0) {
            return new ArrayList<>();
        }
        if (elements.size() == 1 && elements.get(0) == EWElements.EMPTY) {
            return new ArrayList<>();
        }
        Comparator<Element> rareLevel = Comparator.comparingInt(o -> o.rareLevel);
        Comparator<Element> level = Comparator.comparingInt(o -> o.level);
        Comparator<Element> name = Comparator.comparing(o -> o.name);
        elements.sort(rareLevel.thenComparing(level).thenComparing(name));
        MutableText text;
        List<Text> list = new ArrayList<>();
        if (prefix || !lineBreak) {
            text = prefix ? Text.translatable("element.elemworld.name.prefix").formatted(Formatting.WHITE) : Text.empty();
            for (Element element : elements) {
                Text translationName = element.getTranslationName();
                if (translationName != null) {
                    appendText(text, element, translationName);
                }
            }
            list.add(text);
        } else {
            text = Text.empty();
            int lastLevel = -1;
            Iterator<Element> iterator = elements.iterator();
            while (iterator.hasNext()) {
                Element element = iterator.next();
                Text translationName = element.getTranslationName();
                if (translationName != null) {
                    if (element.rareLevel != lastLevel && lastLevel != -1) {
                        list.add(text);
                        text = Text.empty();
                    }
                    lastLevel = element.rareLevel;
                    appendText(text, element, translationName);
                    if (!iterator.hasNext()) {
                        list.add(text);
                    }
                }
            }
        }
        return list;
    }

    private static void appendText(MutableText text, Element element, Text translationName) {
        if (!text.equals(Text.empty())) {
            text.append(" ");
        }
        int levelInt = Math.max(element.level, 0);
        String levelStr = levelInt > LEVELS.length - 1 ? String.valueOf(element.level) : LEVELS[levelInt];
        Formatting formatting = element.getFormatting();
        Text levelText = formatting == null ? Text.empty() : Text.literal(levelStr).formatted(formatting);
        text.append(translationName).append(levelText);
    }

    @Nullable
    public Text getTranslationName() {
        Formatting color = getFormatting();
        return color == null ? null : Text.translatable(this.getTranslationKey()).formatted(color);
    }

    @Nullable
    public Formatting getFormatting() {
        return switch (this.getRareLevel()) {
            case 0 -> null;
            case 1 -> Formatting.WHITE;
            case 2 -> Formatting.AQUA;
            case 3 -> Formatting.GOLD;
            default -> Formatting.GRAY;
        };
    }

    public String getTranslationKey() {
        return "element.elemworld." + this.getName();
    }

    public Color getColor() {
        return switch (this.getRareLevel()) {
            case 0 -> Color.BLACK;
            case 1 -> Color.WHITE;
            case 2 -> Color.CYAN;
            case 3 -> Color.ORANGE;
            default -> Color.GRAY;
        };
    }

    public static @NotNull Map<Predicate<LivingEntity>, Float> getDamageMultiplierMap() {
        Map<Predicate<LivingEntity>, Float> map = new HashMap<>();
        getRegistrySet().forEach(element -> element.writeDamageMultiplier(map));
        return map;
    }

    public StatusEffect getEffect() {
        return Effect.get(this);
    }

    public void addEffect(LivingEntity target, @Nullable Entity attacker) {
        int sec = this.getEffectTime(target);
        if (sec > 0) {
            StatusEffectInstance effect = new StatusEffectInstance(this.getEffect(), sec * 20, 0);
            target.addStatusEffect(effect, attacker);
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

    public boolean isOf(Element element) {
        return this.equals(element);
    }

    public boolean isIn(Element... elements) {
        return Arrays.asList(elements).contains(this);
    }

    public boolean isIn(@NotNull ArrayList<Element> elements) {
        return elements.contains(this);
    }

    public boolean shouldImmuneOnDeath(LivingEntity entity) {
        return false;
    }

    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        //元素效果
    }

    public void tick(LivingEntity entity) {

    }

    public void postHit(LivingEntity target, PlayerEntity attacker) {
        //物品攻击后
    }

    public void postMine(World world, BlockState state, BlockPos pos, PlayerEntity miner) {

    }

    public void useOnBlock(ItemUsageContext context) {

    }

    public void useOnEntity(PlayerEntity user, LivingEntity entity, Hand hand) {

    }

    public void usageTick(World world, LivingEntity user, int remainingUseTicks) {

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

    public void onElementApplied(@NotNull LivingEntity entity, int slot) {
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

    public void onElementRemoved(@NotNull LivingEntity entity, int slot) {
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

    public boolean isSuitableFor(@NotNull ItemStack stack) {
        return stack.isDamageable();
    }

    public boolean isSuitableFor(@NotNull LivingEntity entity) {
        return entity.isAlive();
    }

    public Map<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(int slot) {
        return new HashMap<>();
    }

    public boolean hasEffect() {
        return true;
    }

    public static void register() {
        for (Element element : getRegistrySet()) {
            if (element == EWElements.EMPTY) {
                continue;
            }
            if (!element.hasEffect()) {
                continue;
            }
            Registry.register(Registries.STATUS_EFFECT, id(element.getName()), new Effect(element));
        }
        ServerLifecycleEvents.SERVER_STARTED.register(server -> frozen = true);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> frozen = false);
    }

    private static class Effect extends StatusEffect {

        private final Element element;

        private Effect(@NotNull Element element) {
            super(StatusEffectCategory.HARMFUL, element.getColor().getRGB());
            this.element = element;
        }

        @Override
        public boolean canApplyUpdateEffect(int duration, int amplifier) {
            return this.element.canApplyUpdateEffect(duration, amplifier);
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

        public static StatusEffect get(@NotNull Element element) {
            return Registries.STATUS_EFFECT.get(id(element.name));
        }

        @Override
        public Text getName() {
            return this.element.getTranslationName();
        }
    }
}
