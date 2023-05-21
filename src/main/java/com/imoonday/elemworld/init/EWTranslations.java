package com.imoonday.elemworld.init;

import com.imoonday.elemworld.ElementalWorldData;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;

public class EWTranslations {

    private static final String MODIFY_ELEMENTS_SCREEN = "modify_elements_screen.";
    private static final String COMMANDS = "commands.";

    public static void register() {
        add(MODIFY_ELEMENTS_SCREEN + "no_new", "No new elements", "无新元素");
        add(MODIFY_ELEMENTS_SCREEN + "new", "Added %d elements", "新增 %d 个元素");
        add(MODIFY_ELEMENTS_SCREEN + "cost", "Cost of modification : %d", "锻造花费 : %d");
        add(MODIFY_ELEMENTS_SCREEN + "full", "The item already contains all the elements", "物品已包含所有元素");
        add(MODIFY_ELEMENTS_SCREEN + "not_enough_level", "Refresh elements requires at least level 1 experience", "刷新元素至少要有1级经验");
        add(MODIFY_ELEMENTS_SCREEN + "different_item", "The materials must be the same item", "材料必须为相同物品");
        add(COMMANDS + "add.success", "Add succeeded", "添加成功");
        add(COMMANDS + "add.success.empty", "But what's the point of adding empty element?", "但是添加空元素有什么意义呢？");
        add(COMMANDS + "add.fail", "Add failed (possible cause: already existing/incompatible)", "添加失败(可能原因：已存在/不兼容)");
        add(COMMANDS + "remove.success", "Delete succeeded", "删除成功");
        add(COMMANDS + "remove.fail", "Element does not exist", "元素不存在");
        add(COMMANDS + "get.empty", "Empty", "空");
        add(COMMANDS + "clear.success", "Cleanup succeeded", "清除成功");
        add(COMMANDS + "item.invalid", "The item does not exist", "物品不存在");
        add(COMMANDS + "item.unsupport", "The item is not supported", "物品不支持");
        add("element_visible", "Element display is turned on", "元素显示已开启");
        add("element_invisible", "Element display is turned off", "元素显示已关闭");
        LOGGER.info("Loading Translations");
    }

    private static void add(String key, String en_us, String zh_cn) {
        ElementalWorldData.addTranslation("text.elemworld." + key, en_us, zh_cn);
    }
}
