package com.xiaoguan.train.batch.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ClassName: BusinessFeign
 * Package: com.xiaoguan.train.batch.feign
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/22 21:50
 * @Version 1.0
 */
@FeignClient(name = "business", url = "http://127.0.0.1:8002/business")
public interface BusinessFeign {
    @GetMapping("/hello")
    String hello();
}
