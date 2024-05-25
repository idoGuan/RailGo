package com.xiaoguan.train.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
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
@RefreshScope
public class TestController {

    @Value("${test.nacos}")
    private String testNacos;

    @Autowired
    Environment environment;

    @GetMapping("/hello")
    public String hello(){
        String port = environment.getProperty("local.server.port");

        return "hello " + testNacos + "端口：" + port;
    }
}
