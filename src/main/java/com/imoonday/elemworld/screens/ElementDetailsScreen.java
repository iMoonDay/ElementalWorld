package com.imoonday.elemworld.screens;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.screens.handler.ElementDetailsScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.List;

import static com.imoonday.elemworld.ElementalWorld.id;

public class ElementDetailsScreen extends HandledScreen<ElementDetailsScreenHandler> {

    private static final Identifier TEXTURE = id("textures/gui/element_details.png");

    public ElementDetailsScreen(ElementDetailsScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        List<Text> elementsText = Element.getElementsText(this.handler.getElements(), false, true);
        for (int i = 0; i < elementsText.size(); i++) {
            Text text = elementsText.get(i);
            textRenderer.drawWithShadow(matrices, text, 54, 16 + i * 8, Color.WHITE.getRGB());
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}
