package com.xiaoguan.train.batch.feign;

import com.xiaoguan.train.common.resp.CommonResp;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * ClassName: BusinessFeignFallback
 * Package: com.xiaoguan.train.batch.feign
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/29 10:57
 * @Version 1.0
 */
@Component
public class BusinessFeignFallback implements BusinessFeign{
    @Override
    public String hello() {
        return "Fallback";
    }

    @Override
    public CommonResp<Object> genDaily(Date date) {
        return null;
    }
}
