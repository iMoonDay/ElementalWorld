package com.imoonday.elemworld.elements;

import com.imoonday.elemworld.api.Element;

import java.awt.*;

public class EarthElement extends Element {

    public EarthElement(int maxLevel, int rareLevel, int weight, float miningSpeedMultiplier, float damageMultiplier, float maxHealthMultiplier, float durabilityMultiplier) {
        super(maxLevel, rareLevel, weight, miningSpeedMultiplier, damageMultiplier, maxHealthMultiplier, durabilityMultiplier);
    }

    @Override
    public Color getColor() {
        return Color.DARK_GRAY;
    }
}
