package com.imoonday.elemworld.init;

import com.imoonday.elemworld.ElementalWorldData;

import static com.imoonday.elemworld.ElementalWorld.LOGGER;

public class EWTranslationKeys {

    public static final String NO_NEW_ELEMENTS = registerText("modify_elements_screen.no_new", "No new elements", "无新元素");
    public static final String NEW_ELEMENTS = registerText("modify_elements_screen.new", "Added %d elements", "新增 %d 个元素");
    public static final String COST = registerText("modify_elements_screen.cost", "Cost of modification : %d", "锻造花费 : %d");
    public static final String ELEMENT_FULL = registerText("modify_elements_screen.full", "The item already contains all the elements", "物品已包含所有元素");
    public static final String NOT_ENOUGH_LEVEL = registerText("modify_elements_screen.not_enough_level", "Refresh elements requires at least level 1 experience", "刷新元素至少要有1级经验");
    public static final String NEED_DIFFERENT_ITEM = registerText("modify_elements_screen.different_item", "The materials must be the same item", "材料必须为相同物品");
    public static final String ADD_SUCCESS = registerText("commands.add.success", "Add succeeded", "添加成功");
    public static final String ADD_SUCCESS_EMPTY = registerText("commands.add.success.empty", "But what's the point of adding empty element?", "但是添加空元素有什么意义呢？");
    public static final String ADD_FAIL = registerText("commands.add.fail", "Add failed (possible cause: already existing/incompatible)", "添加失败(可能原因：已存在/不兼容)");
    public static final String REMOVE_SUCCESS = registerText("commands.remove.success", "Delete succeeded", "删除成功");
    public static final String REMOVE_FAIL = registerText("commands.remove.fail", "Element does not exist", "元素不存在");
    public static final String GET_EMPTY = registerText("commands.get.empty", "Empty", "空");
    public static final String CLEAR_SUCCESS = registerText("commands.clear.success", "Cleanup succeeded", "清除成功");
    public static final String INVALID_ITEM = registerText("commands.item.invalid", "The item does not exist", "物品不存在");
    public static final String UNSUPPORTED_ITEM = registerText("commands.item.unsupport", "The item is not supported", "物品不支持");
    public static final String VISIBLE = registerText("element_visible", "Element display is turned on", "元素显示已开启");
    public static final String INVISIBLE = registerText("element_invisible", "Element display is turned off", "元素显示已关闭");
    public static final String GET_NEW_ELEMENT = registerText("get_new_element", "Get a new element: %s", "获得一个新元素: %s");
    public static final String LEVEL_LESS_THAN = registerText("level_less_than", "Your level is less than %s", "等级不足%s级");

    public static final String ELEMENT_INVALID = register("element.elemworld.invalid", "Invalid element: %s", "无效的元素: %s");
    public static final String ELEMENT_NAME_PREFIX = register("element.elemworld.name.prefix", "[Elements]", "[元素]");
    public static final String ELEMENT_FRAGMENT_NAME = register("element.elemworld.name.fragment", "%s Element fragment", "%s元素碎片");

    public static final String KEY_CATEGORY = register("key.categories.elemworld", "Elemental World", "元素世界");
    public static final String KEY_DISPLAY = register("key.elemworld.display", "Open the element screen", "打开元素界面");
    public static final String KEY_TOGGLE_VISIBILITY = register("key.elemworld.toggle_visibility", "Toggle the display of entities' elements", "切换生物元素显示");

    public static void register() {
        LOGGER.info("Loading Translations");
    }

    public static String registerText(String key, String en_us, String zh_cn) {
        return register("text.elemworld." + key, en_us, zh_cn);
    }

    public static String register(String key, String en_us, String zh_cn) {
        return ElementalWorldData.addTranslation(key, en_us, zh_cn);
    }
}
