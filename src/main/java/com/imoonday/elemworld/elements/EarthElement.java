package com.imoonday.elemworld.elements;

import java.awt.*;

public class EarthElement extends Element {

    public EarthElement() {
        super(3, 1, 40, 0.0f, 0.0f, 0.0f, 0.5f);
    }

    @Override
    public Color getColor() {
        return Color.DARK_GRAY;
    }
}
