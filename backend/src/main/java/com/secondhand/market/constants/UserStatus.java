package com.secondhand.market.constants;

/**
 * 用户状态常量
 */
public class UserStatus {
    /** 已禁用 */
    public static final int DISABLED = 0;
    /** 正常 */
    public static final int NORMAL = 1;
    /** 已冻结 */
    public static final int FROZEN = 2;

    /** 已禁用文本 */
    public static final String DISABLED_TEXT = "已禁用";
    /** 正常文本 */
    public static final String NORMAL_TEXT = "正常";
    /** 已冻结文本 */
    public static final String FROZEN_TEXT = "已冻结";

    /**
     * 获取用户状态文本
     */
    public static String getText(int status) {
        switch (status) {
            case DISABLED: return DISABLED_TEXT;
            case NORMAL: return NORMAL_TEXT;
            case FROZEN: return FROZEN_TEXT;
            default: return "未知状态";
        }
    }
}