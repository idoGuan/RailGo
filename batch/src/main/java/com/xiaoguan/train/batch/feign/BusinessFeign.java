package com.xiaoguan.train.batch.feign;

import com.xiaoguan.train.common.resp.CommonResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;

/**
 * ClassName: BusinessFeign
 * Package: com.xiaoguan.train.batch.feign
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/22 21:50
 * @Version 1.0
 */
@FeignClient(value = "business", fallback = BusinessFeignFallback.class)
public interface BusinessFeign {
    @GetMapping("/business/hello")
    String hello();

    @GetMapping("/business/admin/daily-train/gen-daily/{date}")
     CommonResp<Object> genDaily(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date);
}
