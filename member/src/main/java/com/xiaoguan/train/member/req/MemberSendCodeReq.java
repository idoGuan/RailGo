package com.xiaoguan.train.member.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * ClassName: MemberRequestReq
 * Package: com.xiaoguan.train.member.req
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/17 7:40
 * @Version 1.0
 */
public class MemberSendCodeReq {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号码格式错误")
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "MemberSendCodeReq{" +
                "mobile='" + mobile + '\'' +
                '}';
    }
}
