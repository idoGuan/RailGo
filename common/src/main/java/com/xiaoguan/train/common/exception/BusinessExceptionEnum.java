package com.xiaoguan.train.common.exception;

/**
 * ClassName: BusinessExceptionEnum
 * Package: com.xiaoguan.train.common.exception
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/17 8:41
 * @Version 1.0
 */
public enum BusinessExceptionEnum {
    MEMBER_MOBILE_EXIST("手机号已注册"),
    MEMBER_MOBILE_NOT_EXIST("请先获取短信验证码"),
    MEMBER_MOBILE_CODE_ERROR("短信验证码错误"),
    BUSINESS_TRAIN_STATION_NAME_UNIQUE_ERROR("同车次站名已存在"),
    BUSINESS_TRAIN_STATION_INDEX_UNIQUE_ERROR("同车次厢号已存在"),

    CONFIRM_ORDER_TICKET_COUNT_ERROR("余票不足"),

    CONFIRM_ORDER_LOCK_FAIL("当前抢票人数过多，请稍候重试"),
    CONFIRM_ORDER_FLOW_EXCEPTION("当前抢票人数太多了，请稍候重试"),
    CONFIRM_ORDER_SK_TOKEN_FAIL("当前抢票人数过多，请5秒后重试"),
    ;

    private String desc;

    BusinessExceptionEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "BusinessExceptionEnum{" +
                "desc='" + desc + '\'' +
                '}';
    }
}
