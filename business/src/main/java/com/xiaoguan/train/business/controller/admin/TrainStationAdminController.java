package com.xiaoguan.train.business.controller.admin;

import com.xiaoguan.train.common.context.LoginMemberContext;
import com.xiaoguan.train.common.resp.CommonResp;
import com.xiaoguan.train.common.resp.PageResp;
import com.xiaoguan.train.business.req.TrainStationQueryReq;
import com.xiaoguan.train.business.req.TrainStationSaveReq;
import com.xiaoguan.train.business.resp.TrainStationQueryResp;
import com.xiaoguan.train.business.service.TrainStationService;
import jakarta.annotation.Resource;
import jakarta.validation.Configuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: TestController
 * Package: com.xiaoguan.train.trainStation.controller
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/14 13:47
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/train-station")
public class TrainStationAdminController {

    @Resource
    private TrainStationService trainStationService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainStationSaveReq req){
        trainStationService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainStationQueryResp>> queryList(@Valid TrainStationQueryReq req){
        PageResp<TrainStationQueryResp> trainStationQueryRespList = trainStationService.queryList(req);
        return new CommonResp<>(trainStationQueryRespList);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id){
        trainStationService.delete(id);
        return new CommonResp<>();
    }
}