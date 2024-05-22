package com.xiaoguan.train.business.controller.admin;

import com.xiaoguan.train.common.context.LoginMemberContext;
import com.xiaoguan.train.common.resp.CommonResp;
import com.xiaoguan.train.common.resp.PageResp;
import com.xiaoguan.train.business.req.DailyTrainStationQueryReq;
import com.xiaoguan.train.business.req.DailyTrainStationSaveReq;
import com.xiaoguan.train.business.resp.DailyTrainStationQueryResp;
import com.xiaoguan.train.business.service.DailyTrainStationService;
import jakarta.annotation.Resource;
import jakarta.validation.Configuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: TestController
 * Package: com.xiaoguan.train.dailyTrainStation.controller
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/14 13:47
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/daily-train-station")
public class DailyTrainStationAdminController {

    @Resource
    private DailyTrainStationService dailyTrainStationService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainStationSaveReq req){
        dailyTrainStationService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainStationQueryResp>> queryList(@Valid DailyTrainStationQueryReq req){
        PageResp<DailyTrainStationQueryResp> dailyTrainStationQueryRespList = dailyTrainStationService.queryList(req);
        return new CommonResp<>(dailyTrainStationQueryRespList);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id){
        dailyTrainStationService.delete(id);
        return new CommonResp<>();
    }
}