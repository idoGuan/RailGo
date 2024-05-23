package com.xiaoguan.train.business.controller;

import com.xiaoguan.train.business.resp.StationQueryResp;
import com.xiaoguan.train.business.service.StationService;
import com.xiaoguan.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName: TestController
 * Package: com.xiaoguan.train.station.controller
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/14 13:47
 * @Version 1.0
 */
@RestController
@RequestMapping("/station")
public class StationController {

    @Resource
    private StationService stationService;

    @GetMapping("/query-all")
    public CommonResp<List<StationQueryResp>> queryAll(){
        List<StationQueryResp> stationQueryRespList = stationService.queryAll();
        return new CommonResp<>(stationQueryRespList);
    }
}