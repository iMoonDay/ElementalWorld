package com.imoonday.elemworld.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;

public class ElementRendererGui {

    private final MinecraftClient mc;
    private static boolean visible;
    private final TextRenderer fontRenderer;

    public ElementRendererGui() {
        mc = MinecraftClient.getInstance();
        fontRenderer = mc.textRenderer;
        visible = true;
    }

    public static void toggleVisibility() {
        visible = !visible;
    }

    public void onRenderGameOverlayPost(MatrixStack stack, float partialTicks) {
        if (!visible || mc.options.debugEnabled || mc.options.hudHidden) {
            return;
        }
        ClientPlayerEntity player = mc.player;
        if (player == null) {
            return;
        }
//        Set<ElementInstance> elements = player.getElements();
//        List<Text> texts = Element.getElementsText(elements, false, true);
//        for (int i = 0; i < texts.size(); i++) {
//            Text text = texts.get(i);
//            fontRenderer.draw(stack, text, 2, 2 + i * 10, Color.WHITE.getRGB());
//        }
    }
}
