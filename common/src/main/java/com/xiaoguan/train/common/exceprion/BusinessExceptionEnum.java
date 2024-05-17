package com.xiaoguan.train.common.exceprion;

/**
 * ClassName: BusinessExceptionEnum
 * Package: com.xiaoguan.train.common.exceprion
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/17 8:41
 * @Version 1.0
 */
public enum BusinessExceptionEnum {
    MEMBER_MOBILE_EXIST("手机号已注册");

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
