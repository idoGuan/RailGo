package com.xiaoguan.train.member.req;

import jakarta.validation.constraints.NotBlank;

/**
 * ClassName: MemberRequestReq
 * Package: com.xiaoguan.train.member.req
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/17 7:40
 * @Version 1.0
 */
public class MemberRequestReq {
    @NotBlank(message = "【手机号】不能为空")
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "MemberRequestReq{" +
                "mobile='" + mobile + '\'' +
                '}';
    }
}
