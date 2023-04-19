package com.imoonday.elemworld.screens;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.screens.handler.ModifyElementsScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;

import static com.imoonday.elemworld.ElementalWorld.id;

public class ModifyElementsScreen extends HandledScreen<ModifyElementsScreenHandler> {

    private static final Identifier TEXTURE = id("textures/gui/modify_elements.png");

    public ModifyElementsScreen(ModifyElementsScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.handler.cannotUseButton()) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        if (isOnButton(mouseX, mouseY)) {
            if (this.client != null && this.client.interactionManager != null && this.client.player != null) {
                boolean success = this.handler.onButtonClick(this.client.player, 0);
                this.client.interactionManager.clickButton(this.handler.syncId, 0);
                if (success) {
                    this.client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
                }
                this.handler.updateToClient();
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        if (this.handler.cannotUseButton()) {
            drawTexture(matrices, x + 127, y + 54, 176, 0, 20, 18);
        } else if (isOnButton(mouseX, mouseY)) {
            drawTexture(matrices, x + 127, y + 54, 176, 36, 20, 18);
        } else {
            drawTexture(matrices, x + 127, y + 54, 176, 18, 20, 18);
        }
        ArrayList<Element> elements = new ArrayList<>(this.handler.getResult().getStack(0).getElements());
        elements.removeAll(this.handler.getInput().getStack(0).getElements());
        MutableText text = Element.getElementsText(elements);
        if (!this.handler.getInput().getStack(0).isEmpty() || !this.handler.getResult().getStack(0).isEmpty()) {
            if (text == null) {
                text = Text.literal("无新元素");
            }
            int textWidth = textRenderer.getWidth(text);
            int half = textWidth / 2;
            int startX = x + 128 + 9 - half;
            int endX = startX + textWidth;
            if (endX > x + backgroundWidth) {
                startX = x + 86;
            }
            textRenderer.draw(matrices, text, startX, y + 23, Color.black.getRGB());
        }
        if (this.handler.getStack().getElements().size() >= Element.MAX_SIZE) {
            Text full = Text.literal("物品已包含所有元素").formatted(Formatting.RED);
            textRenderer.draw(matrices, full, x + 8, y + 54, Color.WHITE.getRGB());
        } else if (this.handler.getPlayer().experienceLevel < 1 && !this.handler.getStack().isEmpty() && this.handler.getMaterial().isOf(Items.DIAMOND) && !this.handler.getPlayer().getAbilities().creativeMode) {
            Text buttonCost = Text.literal("刷新元素至少要有1级经验").formatted(Formatting.RED);
            textRenderer.draw(matrices, buttonCost, x + 8, y + 54, Color.WHITE.getRGB());
        }
        if (!this.handler.getResult().isEmpty()) {
            Formatting color = this.handler.getPlayer().experienceLevel >= this.handler.getRequiredLevel() || this.handler.getPlayer().getAbilities().creativeMode ? Formatting.GREEN : Formatting.RED;
            Text cost = Text.literal("修改花费 : " + this.handler.getRequiredLevel()).formatted(color);
            textRenderer.draw(matrices, cost, x + backgroundWidth - textRenderer.getWidth(cost) - 8, y + 73, Color.WHITE.getRGB());
        }
    }

    private boolean isOnButton(double mouseX, double mouseY) {
        int y = (height - backgroundHeight) / 2;
        int x = (width - backgroundWidth) / 2;
        return mouseX >= x + 127 && mouseX <= x + 127 + 20 && mouseY >= y + 54 && mouseY <= y + 54 + 18;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}
