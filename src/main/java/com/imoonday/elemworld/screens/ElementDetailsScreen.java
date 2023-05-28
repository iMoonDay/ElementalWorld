package com.imoonday.elemworld.screens;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWTranslationKeys;
import com.imoonday.elemworld.screens.handler.ElementDetailsScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.Set;

import static com.imoonday.elemworld.init.EWIdentifiers.id;

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
        LivingEntity livingEntity = this.handler.getLivingEntity();
        float damage = 1.0f;
        float health = 1.0f;
        int count = 0;
        for (int i = 0; i < 5; i++) {
            ItemStack stack = this.handler.getSlot(i).getStack();
            damage += stack.getDamageMultiplier(player) - 1;
            health += stack.getMaxHealthMultiplier(player) - 1;
            Set<Element.Entry> entries = stack.getElements();
            int size = entries.size();
            if (size != 1 || !entries.iterator().next().element().isInvalid()) {
                count += size;
            }
        }
        textRenderer.draw(matrices, Text.translatable(EWTranslationKeys.ELEMENT_COUNT, count).formatted(Formatting.BOLD), x + 44, y + 18, Color.WHITE.getRGB());
        textRenderer.draw(matrices, Text.translatable(EWTranslationKeys.ELEMENT_DAMAGE_BEHIND, String.format("%.2f", damage)).formatted(Formatting.DARK_GREEN, Formatting.BOLD), x + 44, y + 30, Color.WHITE.getRGB());
        textRenderer.draw(matrices, Text.translatable(EWTranslationKeys.ELEMENT_MAX_HEALTH_BEHIND, String.format("%.2f", health)).formatted(Formatting.BLUE, Formatting.BOLD), x + 44, y + 42, Color.WHITE.getRGB());
        int i = livingEntity.getImmuneCooldown() / 20;
        if (i > 0) {
            String text = i + "s";
            textRenderer.draw(matrices, text, x + 16 - (float) textRenderer.getWidth(text) / 2, y + 44, Color.GREEN.getRGB());
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
