package com.xiaoguan.train.member.controller;

import com.xiaoguan.train.common.context.LoginMemberContext;
import com.xiaoguan.train.common.resp.CommonResp;
import com.xiaoguan.train.common.resp.PageResp;
import com.xiaoguan.train.member.req.TicketQueryReq;
import com.xiaoguan.train.member.resp.TicketQueryResp;
import com.xiaoguan.train.member.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

;

@RestController
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("/query-list")
    public CommonResp<PageResp<TicketQueryResp>> query(@Valid TicketQueryReq req) {
        CommonResp<PageResp<TicketQueryResp>> commonResp = new CommonResp<>();
        req.setMemberId(LoginMemberContext.getId());
        PageResp<TicketQueryResp> pageResp = ticketService.queryList(req);
        commonResp.setContent(pageResp);
        return commonResp;
    }

}
