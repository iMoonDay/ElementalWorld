package com.imoonday.elemworld.elements;

import java.awt.*;

public class EmptyElement extends Element {
    public EmptyElement() {
        super(0, 0, 50);
    }

    @Override
    public Color getColor() {
        return Color.WHITE;
    }

    @Override
    public boolean isInvalid() {
        return true;
    }

    @Override
    public boolean hasEffect() {
        return false;
    }

    @Override
    public boolean hasFragmentItem() {
        return false;
    }
}
