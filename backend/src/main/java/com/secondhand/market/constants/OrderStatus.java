package com.secondhand.market.constants;

/**
 * 订单状态常量
 */
public class OrderStatus {
    /** 待付款 */
    public static final int PENDING_PAYMENT = 0;
    /** 待发货 */
    public static final int PENDING_SHIPMENT = 1;
    /** 待收货 */
    public static final int PENDING_RECEIVE = 2;
    /** 已完成 */
    public static final int COMPLETED = 3;
    /** 已取消 */
    public static final int CANCELLED = 4;

    /** 待付款文本 */
    public static final String PENDING_PAYMENT_TEXT = "待付款";
    /** 待发货文本 */
    public static final String PENDING_SHIPMENT_TEXT = "待发货";
    /** 待收货文本 */
    public static final String PENDING_RECEIVE_TEXT = "待收货";
    /** 已完成文本 */
    public static final String COMPLETED_TEXT = "已完成";
    /** 已取消文本 */
    public static final String CANCELLED_TEXT = "已取消";

    /**
     * 获取订单状态文本
     */
    public static String getText(int status) {
        switch (status) {
            case PENDING_PAYMENT: return PENDING_PAYMENT_TEXT;
            case PENDING_SHIPMENT: return PENDING_SHIPMENT_TEXT;
            case PENDING_RECEIVE: return PENDING_RECEIVE_TEXT;
            case COMPLETED: return COMPLETED_TEXT;
            case CANCELLED: return CANCELLED_TEXT;
            default: return "未知状态";
        }
    }
}