package com.secondhand.market.constants;

/**
 * 商品状态常量
 */
public class ProductStatus {
    /** 已下架 */
    public static final int OFF_SHELF = 0;
    /** 在售 */
    public static final int ON_SHELF = 1;
    /** 已售出 */
    public static final int SOLD_OUT = 2;

    /** 已下架文本 */
    public static final String OFF_SHELF_TEXT = "已下架";
    /** 在售文本 */
    public static final String ON_SHELF_TEXT = "在售";
    /** 已售出文本 */
    public static final String SOLD_OUT_TEXT = "已售出";

    /**
     * 获取商品状态文本
     */
    public static String getText(int status) {
        switch (status) {
            case OFF_SHELF: return OFF_SHELF_TEXT;
            case ON_SHELF: return ON_SHELF_TEXT;
            case SOLD_OUT: return SOLD_OUT_TEXT;
            default: return "未知状态";
        }
    }
}