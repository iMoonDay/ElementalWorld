package com.imoonday.elemworld.effect;

import com.imoonday.elemworld.api.Element;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

import static com.imoonday.elemworld.ElementalWorld.id;

public class ElementEffect extends StatusEffect {

    private final Element element;

    public ElementEffect(Element element) {
        super(StatusEffectCategory.NEUTRAL, element.getColor().getRGB());
        this.element = element;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        switch (this.element) {
            case FIRE -> {
                entity.setOnFireFor(1);
            }
        }
    }

    public static void register() {
        for (Element element : Element.values()) {
            if (element == Element.INVALID) {
                continue;
            }
            Registry.register(Registries.STATUS_EFFECT, id(element.getName()), new ElementEffect(element));
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
