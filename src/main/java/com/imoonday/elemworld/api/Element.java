package com.imoonday.elemworld.api;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.imoonday.elemworld.init.EWIdentifiers.id;

@SuppressWarnings("unused")
public abstract class Element {

    private static final ConcurrentHashMap<String, ? extends Element> ELEMENTS = new ConcurrentHashMap<>();
    private static volatile boolean frozen = false;
    public static final String[] LEVELS = {"", "-I", "-II", "-III", "-IV", "-V", "-VI", "-VII", "-VIII", "-IX", "-X"};
    private String name = "null";
    protected final int maxLevel;
    protected final int rareLevel;
    protected final int weight;
    protected final float miningSpeedMultiplier;
    protected final float damageMultiplier;
    protected final float maxHealthMultiplier;
    protected final float durabilityMultiplier;

    public Element(int maxLevel, int rareLevel, int weight) {
        this(maxLevel, rareLevel, weight, 0.0f, 0.0f, 0.0f, 0.0f);
    }

    public Element(Element element) {
        this(element.maxLevel, element.rareLevel, element.weight, element.miningSpeedMultiplier, element.damageMultiplier, element.maxHealthMultiplier, element.durabilityMultiplier);
        this.name = element.name;
    }

    public Element(int maxLevel, int rareLevel, int weight, float miningSpeedMultiplier, float damageMultiplier, float maxHealthMultiplier, float durabilityMultiplier) {
        this.maxLevel = maxLevel;
        this.rareLevel = rareLevel;
        this.weight = weight;
        this.miningSpeedMultiplier = miningSpeedMultiplier;
        this.damageMultiplier = damageMultiplier;
        this.maxHealthMultiplier = maxHealthMultiplier;
        this.durabilityMultiplier = durabilityMultiplier;
    }

    public static void register() {
        for (Element element : getSortedElements(getRegistrySet().stream().map(element1 -> new ElementInstance(element1, element1.getMaxLevel())).collect(Collectors.toSet())).keySet()) {
            if (element.isInvalid()) {
                continue;
            }
            if (element.hasEffect()) {
                Identifier id = id(element.getName());
                ElementEffect effect = Registry.register(Registries.STATUS_EFFECT, id, new ElementEffect(element));
                Registry.register(Registries.POTION, id, new ElementPotion(element));
            }
            if (element.hasFragmentItem()) {
                EWItems.register(element.getFragmentId(), new ElementFragmentItem(element));
            }
        }
        ServerLifecycleEvents.SERVER_STARTED.register(server -> frozen = true);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> frozen = false);
    }

    public static ImmutableMap<String, ? extends Element> getRegistryMap() {
        return ImmutableMap.copyOf(ELEMENTS);
    }

    public static ImmutableSet<? extends Element> getRegistrySet() {
        ArrayList<? extends Element> elements = new ArrayList<>(ELEMENTS.values());
        return ImmutableSet.copyOf(elements);
    }

    public static int getSizeOf(Predicate<Element> predicate) {
        return ELEMENTS.values().stream().filter(predicate).collect(Collectors.toSet()).size();
    }

    public static <T extends Element> T register(String name, T element) {
        if (frozen) throw new IllegalStateException("Registry is already frozen");
        if (ELEMENTS.containsKey(name)) throw new IllegalStateException("The name is already registered");
        Element value = element.withName(name);
        ELEMENTS.put(name, cast(value));
        return cast(value);
    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object o) {
        return (T) o;
    }

    Element withName(String name) {
        this.name = name;
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

    public float getMiningSpeedMultiplier(World world, LivingEntity entity, BlockState state) {
        return miningSpeedMultiplier;
    }

    public float getDamageMultiplier(World world, LivingEntity entity, LivingEntity target) {
        return damageMultiplier;
    }

    public float getMaxHealthMultiplier(World world, LivingEntity entity) {
        return maxHealthMultiplier;
    }

    public float getDurabilityMultiplier() {
        return durabilityMultiplier;
    }

    public int getWeight() {
        return weight;
    }

    public float getDamageProtectionMultiplier(DamageSource source, LivingEntity entity) {
        return 1.0f;
    }

    public void getPersistentEffects(Map<StatusEffect, Integer> effects) {

    }

    public UUID getUuid(int slot) {
        return UUID.nameUUIDFromBytes((this.getName() + " " + slot).getBytes(StandardCharsets.UTF_8));
    }

    public static Optional<Element> byName(String name) {
        Element element = getRegistryMap().get(name);
        if (element == null) {
            return Optional.empty();
        }
        return Optional.of(element);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "{" + "name='" + name + '\'' + ", maxLevel=" + maxLevel + ", rareLevel=" + rareLevel + ", weight=" + weight + ", miningSpeedMultiplier=" + miningSpeedMultiplier + ", damageMultiplier=" + damageMultiplier + ", maxHealthMultiplier=" + maxHealthMultiplier + ", durabilityMultiplier=" + durabilityMultiplier + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Element element)) return false;
        return maxLevel == element.maxLevel && rareLevel == element.rareLevel && weight == element.weight && Float.compare(element.miningSpeedMultiplier, miningSpeedMultiplier) == 0 && Float.compare(element.damageMultiplier, damageMultiplier) == 0 && Float.compare(element.maxHealthMultiplier, maxHealthMultiplier) == 0 && Float.compare(element.durabilityMultiplier, durabilityMultiplier) == 0 && Objects.equals(name, element.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, maxLevel, rareLevel, weight, miningSpeedMultiplier, damageMultiplier, maxHealthMultiplier, durabilityMultiplier);
    }

    public int getRandomLevel() {
        if (this.maxLevel <= 1) {
            return Math.max(this.maxLevel, 0);
        }
        WeightRandom<Integer> random = WeightRandom.create();
        for (int i = 1; i <= this.maxLevel; i++) {
            random.add(i, this.weight * (this.maxLevel - i + 1));
        }
        return Optional.ofNullable(random.next()).orElse(0);
    }

    public static List<Text> getElementsText(Set<ElementInstance> elements, boolean prefix, boolean lineBreak) {
        if (elements.size() == 0) {
            return new ArrayList<>();
        }
        if (elements.size() == 1 && elements.iterator().next().element().isInvalid()) {
            return new ArrayList<>();
        }
        LinkedHashMap<Element, Integer> sortedElements = getSortedElements(elements);
        MutableText text;
        List<Text> list = new ArrayList<>();
        text = prefix ? Text.translatable("element.elemworld.name.prefix").formatted(Formatting.WHITE) : Text.empty();
        int lastLevel = -1;
        Iterator<Map.Entry<Element, Integer>> iterator = sortedElements.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Element, Integer> entry = iterator.next();
            Element element = entry.getKey();
            int level = entry.getValue();
            Text translationName = element.getTranslationName();
            if (translationName == null) {
                continue;
            }
            if (lineBreak) {
                if (element.rareLevel != lastLevel && lastLevel != -1) {
                    list.add(text);
                    text = Text.empty();
                }
                lastLevel = element.rareLevel;
            }
            if (!text.equals(Text.empty())) {
                text.append(" ");
            }
            int levelInt = Math.max(level, 0);
            String levelStr = levelInt > LEVELS.length - 1 ? "-" + level : LEVELS[levelInt];
            Formatting formatting = element.getFormatting();
            Text levelText = formatting == null ? Text.empty() : Text.literal(levelStr).formatted(formatting);
            text.append(translationName).append(levelText);
            if (!iterator.hasNext()) {
                list.add(text);
            }
        }
        return list;
    }

    public static LinkedHashMap<Element, Integer> getSortedElements(Set<ElementInstance> elements) {
        List<ElementInstance> list = new ArrayList<>(elements);
        Comparator<ElementInstance> rareLevel = Comparator.comparingInt(o -> o.element().rareLevel);
        Comparator<ElementInstance> level = Comparator.comparingInt(ElementInstance::level);
        Comparator<ElementInstance> name = Comparator.comparing(o -> o.element().name);
        list.sort(rareLevel.thenComparing(level).thenComparing(name));
        return list.stream().collect(Collectors.toMap(ElementInstance::element, ElementInstance::level, (a, b) -> b, LinkedHashMap::new));
    }

    @Nullable
    public Text getTranslationName() {
        Formatting color = getFormatting();
        return color == null ? null : Text.translatable(this.getTranslationKey()).formatted(color);
    }

    @Nullable
    public Formatting getFormatting() {
        return getFormatting(this.rareLevel);
    }

    @Nullable
    public static Formatting getFormatting(int rareLevel) {
        return switch (rareLevel) {
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
        return switch (this.rareLevel) {
            case 0 -> Color.BLACK;
            case 1 -> Color.WHITE;
            case 2 -> Color.CYAN;
            case 3 -> Color.ORANGE;
            default -> Color.GRAY;
        };
    }

    public StatusEffect getEffect() {
        return Registries.STATUS_EFFECT.get(id(name));
    }

    public void addEffect(LivingEntity target, @Nullable Entity attacker) {
        int sec = this.getEffectTime(target);
        if (sec > 0) {
            StatusEffectInstance effect = new StatusEffectInstance(this.getEffect(), sec * 20, 0);
            target.addStatusEffect(effect, attacker);
        }
    }

    public int getEffectTime(LivingEntity target) {
        return 5;
    }

    public float getEffectChance() {
        return 0.5f;
    }

    public boolean isInvalid() {
        return this.maxLevel <= 0 || this.isOf(EWElements.EMPTY) || "null".equals(this.name);
    }

    public boolean isOf(Element element) {
        return this.equals(element);
    }

    public boolean isIn(Element... elements) {
        return Arrays.asList(elements).contains(this);
    }

    public boolean isIn(Set<Element> elements) {
        return elements.contains(this);
    }

    public boolean immuneOnDeath(LivingEntity entity) {
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

    public void getDamageMultiplier(Map<Predicate<LivingEntity>, Float> map) {

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

    public void onElementApplied(LivingEntity entity, int slot, int level) {
        if (entity.world.isClient) {
            return;
        }
        AttributeContainer attributes = entity.getAttributes();
        Map<EntityAttribute, EntityAttributeModifier> map = new HashMap<>();
        this.getAttributeModifiers(map, slot);
        float multiplier = this.getMaxHealthMultiplier(entity.world, entity);
        if (multiplier != 0.0f) {
            map.put(EntityAttributes.GENERIC_MAX_HEALTH, new EntityAttributeModifier(this.getUuid(slot), this::getTranslationKey, new ElementInstance(this, level).getLevelMultiplier(multiplier), EntityAttributeModifier.Operation.MULTIPLY_BASE));
        }
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : map.entrySet()) {
            EntityAttributeInstance entityAttributeInstance = attributes.getCustomInstance(entry.getKey());
            if (entityAttributeInstance == null) continue;
            EntityAttributeModifier entityAttributeModifier = entry.getValue();
            float percent = entity.getHealth() / entity.getMaxHealth();
            entityAttributeInstance.removeModifier(entityAttributeModifier);
            entityAttributeInstance.addPersistentModifier(new EntityAttributeModifier(entityAttributeModifier.getId(), this.getTranslationKey() + " " + slot, entityAttributeModifier.getValue() * new ElementInstance(this, level).getLevelMultiplier(1.0f), entityAttributeModifier.getOperation()));
            if (entry.getKey().equals(EntityAttributes.GENERIC_MAX_HEALTH)) {
                entity.setHealth(entity.getMaxHealth() * percent);
            }
        }
    }

    public void onElementRemoved(LivingEntity entity, int slot, int level) {
        if (entity.world.isClient) {
            return;
        }
        AttributeContainer attributes = entity.getAttributes();
        Map<EntityAttribute, EntityAttributeModifier> map = new HashMap<>();
        this.getAttributeModifiers(map, slot);
        float multiplier = this.getMaxHealthMultiplier(entity.world, entity);
        if (multiplier != 0.0f) {
            map.put(EntityAttributes.GENERIC_MAX_HEALTH, new EntityAttributeModifier(this.getUuid(slot), this::getTranslationKey, new ElementInstance(this, level).getLevelMultiplier(multiplier), EntityAttributeModifier.Operation.MULTIPLY_BASE));
        }
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : map.entrySet()) {
            EntityAttributeInstance entityAttributeInstance = attributes.getCustomInstance(entry.getKey());
            if (entityAttributeInstance == null) continue;
            float percent = entity.getHealth() / entity.getMaxHealth();
            entityAttributeInstance.removeModifier(entry.getValue());
            if (entry.getKey().equals(EntityAttributes.GENERIC_MAX_HEALTH)) {
                entity.setHealth(entity.getMaxHealth() * percent);
            }
        }
    }

    public boolean isSuitableFor(ItemStack stack) {
        return stack.isDamageable() && stack.getElements().stream().noneMatch(this::conflictsWith);
    }

    public boolean isSuitableFor(LivingEntity entity) {
        return entity.isAlive() && entity.getElements().stream().noneMatch(this::conflictsWith);
    }

    public void getAttributeModifiers(Map<EntityAttribute, EntityAttributeModifier> map, int slot) {

    }

    public boolean hasEffect() {
        return true;
    }

    public boolean conflictsWith(Element element) {
        return false;
    }

    private boolean conflictsWith(ElementInstance instance) {
        return this.conflictsWith(instance.element());
    }

    public boolean hasFragmentItem() {
        return true;
    }

    public Item getFragmentItem() {
        return Registries.ITEM.get(id(this.getFragmentId()));
    }

    public String getFragmentId() {
        return this.name + "_element_fragment";
    }

    public Potion getElementPotion() {
        return Registries.POTION.get(id(this.getName()));
    }

    public int getPotionDuration() {
        return 10 * 20;
    }

    private static class ElementEffect extends StatusEffect {

        private final Element element;

        private ElementEffect(Element element) {
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

        @Override
        public Text getName() {
            return this.element.getTranslationName();
        }

        @Override
        public String getTranslationKey() {
            return this.element.getTranslationKey();
        }
    }

    public static class ElementPotion extends Potion {

        private final Element element;

        private ElementPotion(Element element) {
            super(new StatusEffectInstance(element.getEffect(), element.getPotionDuration()));
            this.element = element;
        }

        @Override
        public String finishTranslationKey(String prefix) {
            return element.getTranslationKey() + "." + prefix.substring(15).replace(".effect.", "");
        }

        public Element getElement() {
            return element;
        }
    }

    private static class ElementFragmentItem extends Item {

        private final Element element;

        private ElementFragmentItem(Element element) {
            super(new FabricItemSettings().maxCount(16));
            this.element = element;
        }

        @Override
        public Text getName() {
            return Text.translatable("element.elemworld.name.fragment", this.element.getTranslationName());
        }

        @Override
        public Text getName(ItemStack stack) {
            return this.getName();
        }
    }
}
