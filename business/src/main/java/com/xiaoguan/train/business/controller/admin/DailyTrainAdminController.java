package com.xiaoguan.train.business.controller.admin;

import com.xiaoguan.train.business.req.DailyTrainQueryReq;
import com.xiaoguan.train.business.req.DailyTrainSaveReq;
import com.xiaoguan.train.business.resp.DailyTrainQueryResp;
import com.xiaoguan.train.business.service.DailyTrainService;
import com.xiaoguan.train.common.resp.CommonResp;
import com.xiaoguan.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * ClassName: TestController
 * Package: com.xiaoguan.train.dailyTrain.controller
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/14 13:47
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/daily-train")
public class DailyTrainAdminController {

    @Resource
    private DailyTrainService dailyTrainService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainSaveReq req){
        dailyTrainService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainQueryResp>> queryList(@Valid DailyTrainQueryReq req){
        PageResp<DailyTrainQueryResp> dailyTrainQueryRespList = dailyTrainService.queryList(req);
        return new CommonResp<>(dailyTrainQueryRespList);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id){
        dailyTrainService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("/gen-daily/{date}")
    public CommonResp<Object> genDaily(
                @PathVariable
                @DateTimeFormat(pattern = "yyyy-MM-dd")
                Date date){
        dailyTrainService.genDaily(date);
        return new CommonResp<>();
    }
}