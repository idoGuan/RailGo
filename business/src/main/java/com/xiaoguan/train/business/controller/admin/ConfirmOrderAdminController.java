package com.xiaoguan.train.business.controller.admin;

import com.xiaoguan.train.common.resp.CommonResp;
import com.xiaoguan.train.common.resp.PageResp;
import com.xiaoguan.train.business.req.ConfirmOrderQueryReq;
import com.xiaoguan.train.business.req.ConfirmOrderDoReq;
import com.xiaoguan.train.business.resp.ConfirmOrderQueryResp;
import com.xiaoguan.train.business.service.ConfirmOrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: TestController
 * Package: com.xiaoguan.train.confirmOrder.controller
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/14 13:47
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/confirm-order")
public class ConfirmOrderAdminController {

    @Resource
    private ConfirmOrderService confirmOrderService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody ConfirmOrderDoReq req){
        confirmOrderService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<ConfirmOrderQueryResp>> queryList(@Valid ConfirmOrderQueryReq req){
        PageResp<ConfirmOrderQueryResp> confirmOrderQueryRespList = confirmOrderService.queryList(req);
        return new CommonResp<>(confirmOrderQueryRespList);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id){
        confirmOrderService.delete(id);
        return new CommonResp<>();
    }
}