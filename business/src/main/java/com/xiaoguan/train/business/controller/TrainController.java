package com.xiaoguan.train.business.controller;

import com.xiaoguan.train.business.req.TrainQueryReq;
import com.xiaoguan.train.business.resp.TrainQueryResp;
import com.xiaoguan.train.business.service.TrainService;
import com.xiaoguan.train.common.resp.CommonResp;
import com.xiaoguan.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName: TestController
 * Package: com.xiaoguan.train.train.controller
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/14 13:47
 * @Version 1.0
 */
@RestController
@RequestMapping("/train")
public class TrainController {

    @Resource
    private TrainService trainService;

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainQueryResp>> queryList(@Valid TrainQueryReq req){
        PageResp<TrainQueryResp> trainQueryRespList = trainService.queryList(req);
        return new CommonResp<>(trainQueryRespList);
    }

    @GetMapping("/query-all")
    public CommonResp<List<TrainQueryResp>> queryAll(){
        List<TrainQueryResp> trainQueryRespList = trainService.queryAll();
        return new CommonResp<>(trainQueryRespList);
    }

}