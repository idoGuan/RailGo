package com.xiaoguan.train.business.controller.admin;

import com.xiaoguan.train.common.context.LoginMemberContext;
import com.xiaoguan.train.common.resp.CommonResp;
import com.xiaoguan.train.common.resp.PageResp;
import com.xiaoguan.train.business.req.DailyTrainSeatQueryReq;
import com.xiaoguan.train.business.req.DailyTrainSeatSaveReq;
import com.xiaoguan.train.business.resp.DailyTrainSeatQueryResp;
import com.xiaoguan.train.business.service.DailyTrainSeatService;
import jakarta.annotation.Resource;
import jakarta.validation.Configuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: TestController
 * Package: com.xiaoguan.train.dailyTrainSeat.controller
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/14 13:47
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/daily-train-seat")
public class DailyTrainSeatAdminController {

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainSeatSaveReq req){
        dailyTrainSeatService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainSeatQueryResp>> queryList(@Valid DailyTrainSeatQueryReq req){
        PageResp<DailyTrainSeatQueryResp> dailyTrainSeatQueryRespList = dailyTrainSeatService.queryList(req);
        return new CommonResp<>(dailyTrainSeatQueryRespList);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id){
        dailyTrainSeatService.delete(id);
        return new CommonResp<>();
    }
}