package com.xiaoguan.train.business.controller;

import org.springframework.web.bind.annotation.GetMapping;
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
public class TestController {
    @GetMapping("/hello")
    public String hello() throws InterruptedException {
        Thread.sleep(300);
        return "hello world! Business";
    }
}
