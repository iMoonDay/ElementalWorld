package com.imoonday.elemworld.api;

import com.imoonday.elemworld.init.EWElements;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public record ElementEntry(Element element, int level) {

    public static final String NAME_KEY = "Name";
    public static final String LEVEL_KEY = "Level";
    public static final String[] LEVELS = {"", "-I", "-II", "-III", "-IV", "-V", "-VI", "-VII", "-VIII", "-IX", "-X"};
    public static final ElementEntry EMPTY = new ElementEntry(EWElements.EMPTY, 0);

    public ElementEntry(@NotNull Element element, int level) {
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

    public static Optional<ElementEntry> fromNbt(NbtCompound nbt) {
        if (nbt.contains(NAME_KEY, NbtElement.STRING_TYPE)) {
            String name = nbt.getString(NAME_KEY);
            Element element = Element.getRegistryMap().get(name);
            if (element != null && nbt.contains(LEVEL_KEY, NbtElement.INT_TYPE)) {
                return Optional.of(new ElementEntry(element, nbt.getInt(LEVEL_KEY)));
            }
        }
        return Optional.empty();
    }

    public static ElementEntry createRandom(Predicate<Element> predicate) {
        return WeightRandom.getRandom(Element.getRegistrySet(true), predicate, Element::getWeight).orElse(EWElements.EMPTY).withRandomLevel();
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
