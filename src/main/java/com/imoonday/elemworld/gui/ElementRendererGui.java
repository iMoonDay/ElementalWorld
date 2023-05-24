package com.imoonday.elemworld.gui;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWTranslationKeys;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class ElementRendererGui {

    public static final ElementRendererGui INSTANCE = new ElementRendererGui();
    private final MinecraftClient mc;
    private boolean visible = true;

    public ElementRendererGui() {
        mc = MinecraftClient.getInstance();
    }

    public void toggleVisibility() {
        visible = !visible;
        if (mc.player != null) {
            mc.player.sendMessage(Text.translatable(visible ? EWTranslationKeys.VISIBLE : EWTranslationKeys.INVISIBLE), true);
        }
    }

    public void onRenderGameOverlayPost(MatrixStack stack) {
        if (mc.options.debugEnabled || mc.options.hudHidden || !visible) {
            return;
        }
        if (mc.player == null) {
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
