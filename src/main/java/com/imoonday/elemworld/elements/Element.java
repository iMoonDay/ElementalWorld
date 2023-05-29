package com.imoonday.elemworld.elements;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.imoonday.elemworld.ElementalWorldData;
import com.imoonday.elemworld.api.EWRegistry;
import com.imoonday.elemworld.api.Translation;
import com.imoonday.elemworld.api.WeightRandom;
import com.imoonday.elemworld.entities.AbstractElementalEnergyBallEntity;
import com.imoonday.elemworld.init.EWElements;
import com.imoonday.elemworld.init.EWItemGroups;
import com.imoonday.elemworld.init.EWItems;
import com.imoonday.elemworld.init.EWPotions;
import com.imoonday.elemworld.items.ElementBookItem;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.InstantStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.imoonday.elemworld.init.EWIdentifiers.id;
import static com.imoonday.elemworld.init.EWTags.*;
import static com.imoonday.elemworld.init.EWTranslationKeys.*;

@SuppressWarnings("unused")
public abstract class Element {

    private static final ConcurrentHashMap<String, ? extends Element> ELEMENTS = new ConcurrentHashMap<>();
    private static volatile boolean frozen = false;
    private String name = "null";
    public final int maxLevel;
    public final int rareLevel;
    protected final int weight;
    protected final float miningSpeedMultiplier;
    protected final float damageMultiplier;
    protected final float maxHealthMultiplier;
    protected final float durabilityMultiplier;
    private final Translation<Element> translation;

    public Element(int maxLevel, int rareLevel, int weight) {
        this(maxLevel, rareLevel, weight, 0.0f, 0.0f, 0.0f, 0.0f);
    }

    public Element(int maxLevel, int rareLevel, int weight, float miningSpeedMultiplier, float damageMultiplier, float maxHealthMultiplier, float durabilityMultiplier) {
        this.maxLevel = maxLevel;
        this.rareLevel = rareLevel;
        this.weight = weight;
        this.miningSpeedMultiplier = miningSpeedMultiplier;
        this.damageMultiplier = damageMultiplier;
        this.maxHealthMultiplier = maxHealthMultiplier;
        this.durabilityMultiplier = durabilityMultiplier;
        this.translation = new Translation<>(this);
    }

    public static void register() {
        FabricLoader.getInstance().getEntrypoints("elemworld", EWRegistry.class)
                .forEach(registry -> EWRegistry.getRegisterElements(registry).forEach(Element::register));
        List<Entry> sortedElements = getSortedElements(getRegistrySet(false).stream().map(element1 -> new Entry(element1, element1.maxLevel)).collect(Collectors.toSet()));
        for (Entry entry : sortedElements) {
            Element element = entry.element();
            if (element.hasEffect()) {
                ElementEffect.register(element);
                ElementPotion.register(element);
                TemporaryElementEffect.register(element);
                PermanentElementEffect.register(element);
            }
            if (element.hasFragmentItem()) {
                ElementFragmentItem.register(element);
            }
        }
        sortedElements.forEach(entry -> ItemGroupEvents.modifyEntriesEvent(EWItemGroups.ELEMENTAL_WORLD).register(content -> content.add(ElementBookItem.fromElements(entry))));
        ServerLifecycleEvents.SERVER_STARTED.register(server -> frozen = true);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> frozen = false);
    }

    public static ImmutableMap<String, Element> getRegistryMap() {
        return ImmutableMap.copyOf(ELEMENTS);
    }

    public static ImmutableSet<Element> getRegistrySet(boolean containsEmpty) {
        ArrayList<? extends Element> elements = new ArrayList<>(ELEMENTS.values());
        if (!containsEmpty) {
            elements.removeIf(element -> element == null || element.isInvalid());
        }
        return ImmutableSet.copyOf(elements);
    }

    public static int getSizeOf(Predicate<Element> predicate) {
        return ELEMENTS.values().stream().filter(predicate).collect(Collectors.toSet()).size();
    }

    /**
     * Please implement {@link EWRegistry} through the class of the entry point "elemworld" and add elements to map of {@link EWRegistry#registerElements(Map)}
     */
    public static <T extends Element> T register(String name, T element, String en_us, String zh_cn) {
        if (frozen) throw new IllegalStateException("Registry is already frozen");
        if (ELEMENTS.containsKey(name)) throw new IllegalStateException("The name is already registered");
        Element finalElement = element.withName(name).addTranslation("en_us", en_us).addTranslation("zh_cn", zh_cn);
        ElementalWorldData.addTranslation(element.getTranslationKey(), en_us, zh_cn);
        ELEMENTS.putIfAbsent(name, cast(finalElement));
        return cast(finalElement);
    }

    private static void register(String name, Translation<Element> translation) {
        register(name, translation.getInstance(), null, null);
    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object o) {
        return (T) o;
    }

    Element withName(String name) {
        this.name = name;
        return this;
    }

    public Element addTranslation(String languageCode, String content) {
        if (languageCode != null && content != null) {
            this.translation.add(languageCode, content);
        }
        return this;
    }

    public Entry withLevel(int level) {
        return new Entry(this, level);
    }

    public Entry withRandomLevel() {
        return this.withLevel(this.getRandomLevel());
    }

    public String getName() {
        return name;
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

    public int getWeight(ItemStack stack) {
        return (int) (getWeight() * getWeightMultiplier(stack.getElements()));
    }

    public int getWeight(LivingEntity entity) {
        return (int) (getWeight() * getWeightMultiplier(entity.getElements()));
    }

    public Translation<Element> getTranslation() {
        return translation;
    }

    @Nullable
    public TagKey<Item> getFragmentTagKey() {
        return switch (rareLevel) {
            case 1 -> BASE_ELEMENT_FRAGMENTS;
            case 2 -> ADVANCED_ELEMENT_FRAGMENTS;
            case 3 -> RARE_ELEMENT_FRAGMENTS;
            default -> null;
        };
    }

    /**
     * Element 检测是否包含元素
     * <p>Float 包含检测的元素时本元素的权重乘数
     */
    public Map<Element, Float> getWeightMultiplier(Map<Element, Float> map) {
        return map;
    }

    private float getWeightMultiplier(Set<Entry> entries) {
        float multiplier = 1.0f;
        Map<Element, Float> map = getWeightMultiplier(new HashMap<>());
        for (Entry entry : entries) {
            Element element = entry.element();
            if (map.containsKey(element)) {
                multiplier = multiplier * Math.max(map.get(element), 0);
            }
        }
        return multiplier;
    }

    public float getDamageProtectionMultiplier(DamageSource source, LivingEntity entity) {
        return 1.0f;
    }

    public Map<StatusEffect, Integer> getPersistentEffects(Map<StatusEffect, Integer> effects) {
        return effects;
    }

    public UUID getUuid(int slot) {
        return UUID.nameUUIDFromBytes((this.name + " " + slot).getBytes(StandardCharsets.UTF_8));
    }

    public static Optional<Element> byName(String name) {
        return Optional.ofNullable(getRegistryMap().get(name));
    }

    @Override
    public String toString() {
        return "Element{" + "name='" + name + '\'' + ", maxLevel=" + maxLevel + ", rareLevel=" + rareLevel + ", weight=" + weight + ", miningSpeedMultiplier=" + miningSpeedMultiplier + ", damageMultiplier=" + damageMultiplier + ", maxHealthMultiplier=" + maxHealthMultiplier + ", durabilityMultiplier=" + durabilityMultiplier + '}';
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
        return random.next().orElse(0);
    }

    public static List<MutableText> getElementsText(Set<Entry> elements, boolean prefix, boolean lineBreak) {
        if (elements.size() == 0) {
            return new ArrayList<>();
        }
        if (elements.size() == 1 && elements.iterator().next().element().isInvalid()) {
            return new ArrayList<>();
        }
        List<Entry> sortedElements = getSortedElements(elements);
        MutableText text;
        List<MutableText> list = new ArrayList<>();
        text = prefix ? Text.translatable(ELEMENT_NAME_PREFIX).formatted(Formatting.WHITE) : Text.empty();
        int lastLevel = -1;
        Iterator<Entry> iterator = sortedElements.iterator();
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            Element element = entry.element();
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
            text.append(element.withLevel(entry.level()).getName());
            if (!iterator.hasNext()) {
                list.add(text);
            }
        }
        return list;
    }

    public static List<Entry> getSortedElements(Set<Entry> elements) {
        List<Entry> list = new ArrayList<>(elements);
        Comparator<Entry> rareLevel = Comparator.comparingInt(o -> o.element().rareLevel);
        Comparator<Entry> level = Comparator.comparingInt(Entry::level);
        Comparator<Entry> name = Comparator.comparing(o -> o.element().name);
        list.sort(rareLevel.thenComparing(level).thenComparing(name));
        return list;
    }

    public static void addTrinketElements(boolean repeat, LivingEntity entity, List<Entry> list) {
        try {
            if (FabricLoader.getInstance().isModLoaded("trinkets")) {
                TrinketsApi.getTrinketComponent(entity).ifPresent(component -> {
                    for (Pair<SlotReference, ItemStack> stackPair : component.getAllEquipped()) {
                        for (Entry entry : stackPair.getRight().getElements()) {
                            if (repeat || list.stream().noneMatch(instance1 -> instance1.isElementEqual(entry))) {
                                list.add(entry);
                            }
                        }
                    }
                });
            }
        } catch (Throwable ignored) {

        }
    }

    public Text getTranslationName() {
        if (this.isInvalid()) {
            return Text.empty();
        }
        Formatting color = getFormatting();
        return color == null ? Text.translatable(this.getTranslationKey()) : Text.translatable(this.getTranslationKey()).formatted(color);
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
        return "element.elemworld." + name;
    }

    public abstract Color getColor();

    public StatusEffect getEffect() {
        return Registries.STATUS_EFFECT.get(id(name));
    }

    public static Optional<Element> getElementFrom(StatusEffect effect) {
        return effect instanceof ElementEffect elementEffect ? Optional.of(elementEffect.element) : Optional.empty();
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

    public float getEffectChance(ItemStack stack) {
        return stack.isIn(ELEMENT_TOOLS_AND_WEAPONS) ? 0.75f : 0.5f;
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

    public boolean immuneDamageOnDeath(LivingEntity entity) {
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
        Map<EntityAttribute, EntityAttributeModifier> map = getAttributeModifiers(new HashMap<>(), slot);
        float multiplier = this.getMaxHealthMultiplier(entity.world, entity);
        if (multiplier != 0.0f && slot != 4 && slot != 5) {
            map.put(EntityAttributes.GENERIC_MAX_HEALTH, new EntityAttributeModifier(this.getUuid(slot), this::getTranslationKey, new Entry(this, level).getLevelMultiplier(multiplier), EntityAttributeModifier.Operation.MULTIPLY_BASE));
        }
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : map.entrySet()) {
            EntityAttributeInstance entityAttributeInstance = attributes.getCustomInstance(entry.getKey());
            if (entityAttributeInstance == null) continue;
            EntityAttributeModifier entityAttributeModifier = entry.getValue();
            float percent = entity.getHealth() / entity.getMaxHealth();
            entityAttributeInstance.removeModifier(entityAttributeModifier);
            entityAttributeInstance.addPersistentModifier(new EntityAttributeModifier(entityAttributeModifier.getId(), this.getTranslationKey() + " " + slot, entityAttributeModifier.getValue() * new Entry(this, level).getLevelMultiplier(1.0f), entityAttributeModifier.getOperation()));
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
        Map<EntityAttribute, EntityAttributeModifier> map = getAttributeModifiers(new HashMap<>(), slot);
        float multiplier = this.getMaxHealthMultiplier(entity.world, entity);
        if (multiplier != 0.0f && slot != 4 && slot != 5) {
            map.put(EntityAttributes.GENERIC_MAX_HEALTH, new EntityAttributeModifier(this.getUuid(slot), this::getTranslationKey, new Entry(this, level).getLevelMultiplier(multiplier), EntityAttributeModifier.Operation.MULTIPLY_BASE));
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

    /**
     * @param slot -1:Self 0:Head 1:Chest 2:Legs 3:Feet 4:Mainhand; 5:Offhand
     *             <p>Please check if it works on hand
     */
    public Map<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(Map<EntityAttribute, EntityAttributeModifier> map, int slot) {
        return map;
    }

    public boolean hasEffect() {
        return !this.isInvalid();
    }

    public boolean conflictsWith(Element element) {
        return false;
    }

    private boolean conflictsWith(Entry entry) {
        return this.conflictsWith(entry.element());
    }

    public boolean hasFragmentItem() {
        return !this.isInvalid();
    }

    public Item getFragmentItem() {
        return Registries.ITEM.get(id(this.getFragmentId()));
    }

    public String getFragmentId() {
        return this.name + "_element_fragment";
    }

    public Potion getElementPotion() {
        return Registries.POTION.get(id(name));
    }

    public int getPotionDuration() {
        return 10 * 20;
    }

    public StatusEffect getTemporaryElementEffect() {
        return Registries.STATUS_EFFECT.get(id("temporary_" + name + "_element"));
    }

    public EntityType<? extends AbstractElementalEnergyBallEntity> getEnergyBallEntity() {
        return null;
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

        private static void register(Element element) {
            Registry.register(Registries.STATUS_EFFECT, id(element.name), new ElementEffect(element));
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

        private static void register(Element element) {
            ElementPotion potion = Registry.register(Registries.POTION, id(element.name), new ElementPotion(element));
            String en_us = element.getTranslation().get();
            String zh_cn = element.getTranslation().get("zh_cn");
            ElementalWorldData.addTranslation(element.getTranslationKey() + ".potion", "Potion of " + en_us + " Element", zh_cn + "元素药水");
            ElementalWorldData.addTranslation(element.getTranslationKey() + ".splash_potion", "Splash Potion of " + en_us + " Element", "喷溅型" + zh_cn + "元素药水");
            ElementalWorldData.addTranslation(element.getTranslationKey() + ".lingering_potion", "Lingering Potion of " + en_us + " Element", "滞留型" + zh_cn + "元素药水");
            ElementalWorldData.addTranslation(element.getTranslationKey() + ".tipped_arrow", "Arrow of " + en_us + " Element", zh_cn + "元素之箭");
            EWPotions.addPotion(potion);
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
            return Text.translatable(ELEMENT_FRAGMENT_NAME);
        }

        @Override
        public Text getName(ItemStack stack) {
            return this.getName();
        }

        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            super.appendTooltip(stack, world, tooltip, context);
            tooltip.add(this.element.getTranslationName());
        }

        private static void register(Element element) {
            EWItems.register(element.getFragmentId(), new ElementFragmentItem(element), element.translation.get(), element.translation.get("zh_cn"), ELEMENT_FRAGMENTS, element.getFragmentTagKey());
        }
    }

    public record Entry(@NotNull Element element, int level) {

        public static final String NAME_KEY = "Name";
        public static final String LEVEL_KEY = "Level";
        public static final String[] LEVELS = {"", "-I", "-II", "-III", "-IV", "-V", "-VI", "-VII", "-VIII", "-IX", "-X"};
        public static final Entry EMPTY = new Entry(EWElements.EMPTY, 0);

        public Entry(@NotNull Element element, int level) {
            this.element = element;
            this.level = Math.max(level, 0);
        }

        public Text getName() {
            String str = level > LEVELS.length - 1 ? "-" + level : LEVELS[level];
            Formatting formatting = element.getFormatting();
            return element.getTranslationName().copy().append(formatting == null ? Text.empty() : Text.literal(str).formatted(formatting));
        }

        public float getLevelMultiplier(float multiplier) {
            if (multiplier == 0.0f) return 0.0f;
            float f1 = (float) level / element.maxLevel;
            float f2;
            if ((element.maxLevel > 1)) {
                f2 = ((float) (element.maxLevel - level) / (element.maxLevel - 1));
            } else {
                f2 = 1.0f;
            }
            multiplier *= multiplier >= 0 ? f1 : f2;
            return multiplier;
        }

        public NbtCompound toNbt() {
            NbtCompound nbt = new NbtCompound();
            nbt.putString(NAME_KEY, element.getName());
            nbt.putInt(LEVEL_KEY, level);
            return nbt;
        }

        public static Optional<Entry> fromNbt(NbtCompound nbt) {
            if (nbt.contains(NAME_KEY, NbtElement.STRING_TYPE)) {
                String name = nbt.getString(NAME_KEY);
                Element element = getRegistryMap().get(name);
                if (element != null && nbt.contains(LEVEL_KEY, NbtElement.INT_TYPE)) {
                    return Optional.of(new Entry(element, nbt.getInt(LEVEL_KEY)));
                }
            }
            return Optional.empty();
        }

        public static Entry createRandom(Predicate<Element> predicate, Function<Element, Integer> weightFunc) {
            return WeightRandom.getRandom(getRegistrySet(true), predicate, weightFunc).orElse(EWElements.EMPTY).withRandomLevel();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Entry entry)) return false;
            return level == entry.level && Objects.equals(element, entry.element);
        }

        public static Set<Element> getElementSet(Set<Entry> entries) {
            return entries.stream().map(Entry::element).collect(Collectors.toSet());
        }

        public boolean isElementEqual(Entry entry) {
            return this.element.isOf(entry.element);
        }
    }

    public static class ElementArgumentType implements ArgumentType<Element> {

        public static final DynamicCommandExceptionType INVALID_ELEMENT_EXCEPTION = new DynamicCommandExceptionType(element -> Text.translatable(ELEMENT_INVALID, element));

        @Contract(value = " -> new", pure = true)
        public static @NotNull Element.ElementArgumentType element() {
            return new ElementArgumentType();
        }

        public static Element getElement(@NotNull CommandContext<ServerCommandSource> context, String id) {
            return context.getArgument(id, Element.class);
        }

        @Override
        public Element parse(@NotNull StringReader reader) throws CommandSyntaxException {
            String string = reader.readUnquotedString();
            Optional<Element> element = byName(string);
            if (element.isEmpty()) {
                throw INVALID_ELEMENT_EXCEPTION.create(string);
            }
            return element.get();
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return CommandSource.suggestMatching(getRegistryMap().keySet(), builder);
        }
    }

    private static class TemporaryElementEffect extends StatusEffect {

        private final Element element;

        private TemporaryElementEffect(Element element) {
            super(StatusEffectCategory.BENEFICIAL, element.getColor().getRGB());
            this.element = element;
        }

        @Override
        public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
            entity.addElement(element.withLevel(MathHelper.clamp(amplifier + 1, 1, element.maxLevel)));
        }

        @Override
        public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
            entity.removeElement(element);
        }

        private static void register(Element element) {
            String temporaryEffectId = "temporary_" + element.name + "_element";
            TemporaryElementEffect temporaryEffect = Registry.register(Registries.STATUS_EFFECT, id(temporaryEffectId), new TemporaryElementEffect(element));
            ElementalWorldData.addTranslation(temporaryEffect, "Temporary " + element.getTranslation().get() + " Element", "临时" + element.getTranslation().get("zh_cn") + "元素");
            EWPotions.registerPotion(temporaryEffectId, temporaryEffect);
        }
    }

    private static class PermanentElementEffect extends InstantStatusEffect {

        private final Element element;

        private PermanentElementEffect(Element element) {
            super(StatusEffectCategory.BENEFICIAL, element.getColor().getRGB());
            this.element = element;
        }

        @Override
        public void applyUpdateEffect(LivingEntity entity, int amplifier) {
            addElement(entity, amplifier);
        }

        @Override
        public void applyInstantEffect(@Nullable Entity source, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity) {
            addElement(target, amplifier);
        }

        private void addElement(LivingEntity entity, int amplifier) {
            entity.addElement(element.withLevel(MathHelper.clamp(amplifier + 1, 1, element.maxLevel)));
        }

        private static void register(Element element) {
            String permanentEffectId = "permanent_" + element.name + "_element";
            PermanentElementEffect permanentEffect = Registry.register(Registries.STATUS_EFFECT, id(permanentEffectId), new PermanentElementEffect(element));
            ElementalWorldData.addTranslation(permanentEffect, "Permanent " + element.getTranslation().get() + " Element", "永久" + element.getTranslation().get("zh_cn") + "元素");
            EWPotions.registerPotion(permanentEffectId, permanentEffect);
        }
    }
}
