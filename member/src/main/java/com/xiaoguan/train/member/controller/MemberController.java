package com.xiaoguan.train.member.controller;

import com.xiaoguan.train.member.service.MemberService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: TestController
 * Package: com.xiaoguan.train.member.controller
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/14 13:47
 * @Version 1.0
 */
@RestController
@RequestMapping("/member")
public class MemberController {

    @Resource
    private MemberService memberService;

    @GetMapping("/count")
    public int count(){
        return memberService.count();
    }
}
