package com.imoonday.elemworld.api;

import com.imoonday.elemworld.init.EWElements;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record ElementEntry(Element element, int level) {

    public static final String NAME_KEY = "Name";
    public static final String LEVEL_KEY = "Level";
    public static final ElementEntry EMPTY = new ElementEntry(EWElements.EMPTY, 0);

    public ElementEntry(@NotNull Element element, int level) {
        this.element = element;
        this.level = level;
    }

    public Text getLevelText() {
        int level = Math.max(this.level(), 0);
        String str = level > Element.LEVELS.length - 1 ? "-" + this.level() : Element.LEVELS[level];
        Formatting formatting = this.element().getFormatting();
        return formatting == null ? Text.empty() : Text.literal(str).formatted(formatting);
    }

    public float getLevelMultiplier(float multiplier) {
        if (multiplier == 0.0f) return 0.0f;
        Element element = this.element;
        int level = this.level;
        float f1 = (float) level / element.getMaxLevel();
        float f2 = (element.getMaxLevel() > 1) ? ((float) (element.getMaxLevel() - level) / (element.getMaxLevel() - 1)) : 1.0f;
        multiplier *= multiplier >= 0 ? f1 : f2;
        return multiplier;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString(NAME_KEY, element.getName());
        nbt.putInt(LEVEL_KEY, level);
        return nbt;
    }

    public static Optional<ElementEntry> fromNbt(NbtCompound nbt) {
        if (nbt.contains(NAME_KEY, NbtElement.STRING_TYPE)) {
            String name = nbt.getString(NAME_KEY);
            Element element = Element.getRegistryMap().get(name);
            if (element == null) {
                return Optional.empty();
            }
            if (nbt.contains(LEVEL_KEY, NbtElement.INT_TYPE)) {
                return Optional.of(new ElementEntry(element, nbt.getInt(LEVEL_KEY)));
            }
        }
        return Optional.empty();
    }

    public static ElementEntry createRandomFor(ItemStack stack, boolean exclude) {
        Set<Element> excludes = ElementEntry.getElementSet(stack.getElements());
        Element element = EWElements.EMPTY;
        Element random = WeightRandom.getRandom(Element.getRegistrySet(), element1 -> (!element1.isIn(excludes) || !exclude) && element1.isSuitableFor(stack), Element::getWeight);
        if (random != null) element = random;
        return new ElementEntry(element, element.getRandomLevel());
    }

    public static ElementEntry createRandomFor(LivingEntity entity) {
        Element element = EWElements.EMPTY;
        Element random = WeightRandom.getRandom(Element.getRegistrySet(), element1 -> element1.isSuitableFor(entity), Element::getWeight);
        if (random != null) element = random;
        return new ElementEntry(element, element.getRandomLevel());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElementEntry entry)) return false;
        return level == entry.level && Objects.equals(element, entry.element);
    }

    public static Set<Element> getElementSet(Set<ElementEntry> entries) {
        return entries.stream().map(ElementEntry::element).collect(Collectors.toSet());
    }

    public boolean isElementEqual(ElementEntry entry) {
        return this.element.isOf(entry.element);
    }
}
