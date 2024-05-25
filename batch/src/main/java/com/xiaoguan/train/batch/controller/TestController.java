package com.xiaoguan.train.batch.controller;

import com.xiaoguan.train.batch.feign.BusinessFeign;
import jakarta.annotation.Resource;
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

    @Resource
    private BusinessFeign businessFeign;

    @GetMapping("/hello")
    public String hello(){
        String hello = businessFeign.hello();
        System.out.println(hello);
        return "hello world! Batch" + hello;
    }
}
