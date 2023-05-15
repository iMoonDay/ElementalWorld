package com.imoonday.elemworld.elements;

import com.imoonday.elemworld.api.Element;

import java.awt.*;

public class EmptyElement extends Element {
    public EmptyElement(int weight) {
        super(0, 0, weight);
    }

    @Override
    public Color getColor() {
        return Color.WHITE;
    }

    @Override
    public boolean isInvalid() {
        return true;
    }
}
