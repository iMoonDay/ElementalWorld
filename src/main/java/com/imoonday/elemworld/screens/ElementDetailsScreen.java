package com.imoonday.elemworld.screens;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.screens.handler.ElementDetailsScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.Set;

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
        PlayerEntity player = this.handler.getPlayer();
        float[] multipliers = {1.0f, 1.0f};
        int count = 0;
        for (int i = 0; i < 5; i++) {
            ItemStack stack = this.handler.getSlot(i).getStack();
            multipliers[0] += stack.getDamageMultiplier(player) - 1;
            multipliers[1] += stack.getMaxHealthMultiplier(player) - 1;
            Set<Element> elements = stack.getElements().keySet();
            int size = elements.size();
            if (size == 1 && elements.iterator().next().isInvalid()) {
                continue;
            }
            count += size;
        }
        textRenderer.draw(matrices, Text.literal("元素个数 - " + count).formatted(Formatting.BOLD), x + 44, y + 18, Color.WHITE.getRGB());
        String[] strings = {"攻击伤害", "生命上限"};
        Formatting[] formattings = {Formatting.DARK_GREEN, Formatting.BLUE};
        for (int i = 0; i < 2; i++) {
            Text text = Text.literal(strings[i] + " × " + String.format("%.2f", multipliers[i])).formatted(formattings[i], Formatting.BOLD);
            textRenderer.draw(matrices, text, x + 44, y + 18 + (i + 1) * 12, Color.WHITE.getRGB());
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
