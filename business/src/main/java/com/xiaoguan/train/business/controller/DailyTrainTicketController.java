package com.xiaoguan.train.business.controller;

import com.xiaoguan.train.business.req.DailyTrainTicketQueryReq;
import com.xiaoguan.train.business.resp.DailyTrainTicketQueryResp;
import com.xiaoguan.train.business.service.DailyTrainTicketService;
import com.xiaoguan.train.common.resp.CommonResp;
import com.xiaoguan.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: TestController
 * Package: com.xiaoguan.train.dailyTrainTicket.controller
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/14 13:47
 * @Version 1.0
 */
@RestController
@RequestMapping("/daily-train-ticket")
public class DailyTrainTicketController {

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(@Valid DailyTrainTicketQueryReq req){
        PageResp<DailyTrainTicketQueryResp> dailyTrainTicketQueryRespList = dailyTrainTicketService.queryList(req);
        return new CommonResp<>(dailyTrainTicketQueryRespList);
    }

}