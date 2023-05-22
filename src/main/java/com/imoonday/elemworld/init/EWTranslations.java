package com.imoonday.elemworld.init;

import com.imoonday.elemworld.ElementalWorldData;
import net.minecraft.text.Text;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;

public class EWTranslations {

    private static final Text NO_NEW = register("modify_elements_screen.no_new", "No new elements", "无新元素");
    private static final Text NEW = register("modify_elements_screen.new", "Added %d elements", "新增 %d 个元素");
    private static final Text COST = register("modify_elements_screen.cost", "Cost of modification : %d", "锻造花费 : %d");
    private static final Text FULL = register("modify_elements_screen.full", "The item already contains all the elements", "物品已包含所有元素");
    private static final Text NOT_ENOUGH_LEVEL = register("modify_elements_screen.not_enough_level", "Refresh elements requires at least level 1 experience", "刷新元素至少要有1级经验");
    private static final Text DIFFERENT_ITEM = register("modify_elements_screen.different_item", "The materials must be the same item", "材料必须为相同物品");
    private static final Text ADD_SUCCESS = register("commands.add.success", "Add succeeded", "添加成功");
    private static final Text ADD_SUCCESS_EMPTY = register("commands.add.success.empty", "But what's the point of adding empty element?", "但是添加空元素有什么意义呢？");
    private static final Text ADD_FAIL = register("commands.add.fail", "Add failed (possible cause: already existing/incompatible)", "添加失败(可能原因：已存在/不兼容)");
    private static final Text REMOVE_SUCCESS = register("commands.remove.success", "Delete succeeded", "删除成功");
    private static final Text REMOVE_FAIL = register("commands.remove.fail", "Element does not exist", "元素不存在");
    private static final Text GET_EMPTY = register("commands.get.empty", "Empty", "空");
    private static final Text CLEAR_SUCCESS = register("commands.clear.success", "Cleanup succeeded", "清除成功");
    private static final Text INVALID_ITEM = register("commands.item.invalid", "The item does not exist", "物品不存在");
    private static final Text UNSUPPORTED_ITEM = register("commands.item.unsupport", "The item is not supported", "物品不支持");
    private static final Text VISIBLE = register("element_visible", "Element display is turned on", "元素显示已开启");
    private static final Text INVISIBLE = register("element_invisible", "Element display is turned off", "元素显示已关闭");

    public static void register() {
        LOGGER.info("Loading Translations");
    }

    private static Text register(String key, String en_us, String zh_cn) {
        String translationKey = "text.elemworld." + key;
        ElementalWorldData.addTranslation(translationKey, en_us, zh_cn);
        return Text.translatable(translationKey);
    }
}
