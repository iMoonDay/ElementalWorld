package com.imoonday.elemworld.gui;

import com.imoonday.elemworld.api.Element;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class ElementRendererGui {

    public static final ElementRendererGui INSTANCE = new ElementRendererGui();
    private final MinecraftClient mc;
    private final ClientPlayerEntity player;
    private boolean visible = true;

    public ElementRendererGui() {
        mc = MinecraftClient.getInstance();
        player = mc.player;
    }

    public void toggleVisibility() {
        visible = !visible;
        player.sendMessage(Text.translatable("text.elemworld.element_" + (visible ? "visible" : "invisible")), true);
    }

    public void onRenderGameOverlayPost(MatrixStack stack) {
        if (mc.options.debugEnabled || mc.options.hudHidden || !visible) {
            return;
        }
        if (player == null) {
            return;
        }
        if (!(mc.targetedEntity instanceof LivingEntity living)) {
            return;
        }
        List<Element.Entry> sortedEntries = Element.getSortedElements(living.getElements());
        for (int i = 0; i < sortedEntries.size(); i++) {
            if (i > 15) break;
            Element.Entry entry = sortedEntries.get(i);
            Item item = entry.element().getFragmentItem();
            int y = mc.getWindow().getScaledHeight() / 2 + 8;
            int x = mc.getWindow().getScaledWidth() / 2 - 8 + 16 * i - (sortedEntries.size() - 1) * 16 / 2;
            mc.getItemRenderer().renderInGui(stack, new ItemStack(item), x, y);
        }
    }
}
