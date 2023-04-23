package com.imoonday.elemworld.screens;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.screens.handler.ModifyElementsScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;

import static com.imoonday.elemworld.ElementalWorld.id;

public class ModifyElementsScreen extends HandledScreen<ModifyElementsScreenHandler> {

    private static final Identifier TEXTURE = id("textures/gui/modify_elements.png");
    public static final int BUTTON_X = 74;
    public static final int BUTTON_Y = 54;

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
                    this.client.player.playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
                    this.handler.sendContentUpdates();
                    this.handler.updateToClient();
                }
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
        PlayerEntity player = this.handler.getPlayer();
        if (this.handler.cannotUseButton()) {
            drawTexture(matrices, x + BUTTON_X, y + BUTTON_Y, 176, 0, 20, 18);
        } else if (isOnButton(mouseX, mouseY)) {
            drawTexture(matrices, x + BUTTON_X, y + BUTTON_Y, 176, 36, 20, 18);
        } else {
            drawTexture(matrices, x + BUTTON_X, y + BUTTON_Y, 176, 18, 20, 18);
        }
        if (!this.handler.getSlot(2).canTakeItems(player) || !this.handler.getStack().isEmpty() && this.handler.getResult().isEmpty()) {
            drawTexture(matrices, x + 99, y + 32, 196, 0, 28, 21);
        }
        Text tooltip = getTooltip();
        if (tooltip != null) {
            int i = textRenderer.getWidth(tooltip);
            AnvilScreen.fill(matrices, x + 8, y + 21 - 2, x + 8 + i + 2, y + 21 - 2 + 12, 0x4F000000);
            textRenderer.drawWithShadow(matrices, tooltip, x + 8 + 2, y + 21, Color.RED.getRGB());
        }
        if ((!this.handler.getStack().isEmpty() || !this.handler.getResult().isEmpty()) && tooltip == null) {
            ArrayList<Element> elements = new ArrayList<>(this.handler.getResult().getStack(0).getElements());
            elements.removeAll(this.handler.getInput().getStack(0).getElements());
            Text text = Element.getElementsText(elements, false);
            if (text == null) {
                text = Text.literal("无新元素");
            }
            if (elements.size() > 5) {
                text = Text.literal("新增 " + elements.size() + " 个元素");
            }
            int textWidth = textRenderer.getWidth(text);
            int half = textWidth / 2;
            int startX = x + 133 + 9 - half;
            int endX = startX + textWidth;
            if (endX > x + backgroundWidth - 2) {
                startX = Math.max(x + backgroundWidth - 8 - textWidth, x + 8);
            }
            if (textWidth > backgroundWidth - 16) {
                startX = x + (backgroundWidth / 2) - half;
            }
            AnvilScreen.fill(matrices, startX - 2, y + 21 - 2, startX + textWidth + 2, y + 21 - 2 + 12, 0x4F000000);
            textRenderer.drawWithShadow(matrices, text, startX, y + 21, Color.WHITE.getRGB());
        }
        if (!this.handler.getResult().isEmpty()) {
            Color color = player.experienceLevel >= this.handler.getRequiredLevel() || player.isCreative() ? Color.GREEN : Color.RED;
            Text cost = Text.literal("修改花费 : " + this.handler.getRequiredLevel());
            int x1 = x + backgroundWidth - textRenderer.getWidth(cost) - 8;
            AnvilScreen.fill(matrices, x1 - 4, y + BUTTON_Y + 2, x + this.backgroundWidth - 8, y + BUTTON_Y + 2 + 12, 0x4F000000);
            textRenderer.drawWithShadow(matrices, cost, x1 - 2, y + BUTTON_Y + 4, color.getRGB());
        }
    }

    @Nullable
    private Text getTooltip() {
        PlayerEntity player = this.handler.getPlayer();
        ItemStack material = this.handler.getMaterial();
        ItemStack stack = this.handler.getStack();
        Text tooltip = null;
        if (stack.getElements().size() >= Element.LAST_INDEX) {
            tooltip = Text.literal("物品已包含所有元素");
        } else if (player.experienceLevel < 1 && !stack.isEmpty() && material.isOf(Items.DIAMOND) && !player.isCreative()) {
            tooltip = Text.literal("刷新元素至少要有1级经验");
        } else if (!stack.isOf(material.getItem()) && !stack.isEmpty() && !material.isOf(Items.DIAMOND) && !material.isEmpty()) {
            tooltip = Text.literal("材料必须为相同物品");
        }
        return tooltip;
    }

    private boolean isOnButton(double mouseX, double mouseY) {
        int y = (height - backgroundHeight) / 2;
        int x = (width - backgroundWidth) / 2;
        return mouseX >= x + BUTTON_X && mouseX <= x + BUTTON_X + 20 && mouseY >= y + BUTTON_Y && mouseY <= y + BUTTON_Y + 18;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}
