package com.imoonday.elemworld.gui;

import com.imoonday.elemworld.api.Element;
import com.imoonday.elemworld.api.ElementEntry;
import com.imoonday.elemworld.init.EWItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ElementRendererGui {

    private final MinecraftClient mc;
    private final ClientPlayerEntity player;

    public ElementRendererGui() {
        mc = MinecraftClient.getInstance();
        player = mc.player;
    }

    public void onRenderGameOverlayPost(MatrixStack stack) {
        if (mc.options.debugEnabled || mc.options.hudHidden) {
            return;
        }
        if (player == null || !player.isHolding(EWItems.ELEMENT_DETECTOR)) {
            return;
        }
        if (!(mc.targetedEntity instanceof LivingEntity living)) {
            return;
        }
        List<ElementEntry> sortedEntries = Element.getSortedElements(living.getElements());
        for (int i = 0; i < sortedEntries.size(); i++) {
            if (i > 15) break;
            ElementEntry entry = sortedEntries.get(i);
            Item item = entry.element().getFragmentItem();
            int y = mc.getWindow().getScaledHeight() / 2 + 8;
            int x = mc.getWindow().getScaledWidth() / 2 - 8 + 16 * i - (sortedEntries.size() - 1) * 16 / 2;
            mc.getItemRenderer().renderInGui(stack, new ItemStack(item), x, y);
        }
    }
}
