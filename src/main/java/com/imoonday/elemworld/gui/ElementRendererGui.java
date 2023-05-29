package com.imoonday.elemworld.gui;

import com.imoonday.elemworld.elements.Element;
import com.imoonday.elemworld.init.EWTranslationKeys;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
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

    public void onRenderGameOverlayPost(MatrixStack matrixStack) {
        if (mc.options.debugEnabled || mc.options.hudHidden || !visible) {
            return;
        }
        if (mc.player == null) {
            return;
        }
        if (!(mc.targetedEntity instanceof LivingEntity living)) {
            return;
        }
        List<Element> elements = living.getElements().stream().map(Element.Entry::element).collect(Collectors.toList());
        living.getEffectElements().stream().map(Element.Entry::element).toList().stream().filter(element -> !elements.contains(element)).forEach(elements::add);
        sortElements(elements);
        for (int i = 0; i < elements.size(); i++) {
            if (i > 15) break;
            Element element = elements.get(i);
            displayElement(element, 16, i, matrixStack, elements.size());
        }
    }

    public static void sortElements(List<Element> elements) {
        Comparator<Element> rareLevel = Comparator.comparingInt(o -> o.rareLevel);
        Comparator<Element> name = Comparator.comparing(Element::getName);
        elements.sort(rareLevel.thenComparing(name));
    }

    private void displayElement(Element element, int offsetY, int index, MatrixStack matrixStack, int size) {
        Item item = element.getFragmentItem();
        int y = mc.getWindow().getScaledHeight() / 2 + offsetY;
        int x = mc.getWindow().getScaledWidth() / 2 - 8 + 16 * index - (size - 1) * 16 / 2;
        mc.getItemRenderer().renderInGui(matrixStack, new ItemStack(item), x, y);
    }

    public static void registerClient(){
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> INSTANCE.onRenderGameOverlayPost(matrixStack));
    }
}
