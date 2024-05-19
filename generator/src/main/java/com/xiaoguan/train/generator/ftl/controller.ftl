package com.xiaoguan.train.${module}.controller;

import com.xiaoguan.train.common.context.LoginMemberContext;
import com.xiaoguan.train.common.resp.CommonResp;
import com.xiaoguan.train.common.resp.PageResp;
import com.xiaoguan.train.${module}.req.${Domain}QueryReq;
import com.xiaoguan.train.${module}.req.${Domain}SaveReq;
import com.xiaoguan.train.${module}.resp.${Domain}QueryResp;
import com.xiaoguan.train.${module}.service.${Domain}Service;
import jakarta.annotation.Resource;
import jakarta.validation.Configuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: TestController
 * Package: com.xiaoguan.train.${domain}.controller
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/14 13:47
 * @Version 1.0
 */
@RestController
@RequestMapping("/${do_main}")
public class ${Domain}Controller {

    @Resource
    private ${Domain}Service ${domain}Service;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody ${Domain}SaveReq req){
        ${domain}Service.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<${Domain}QueryResp>> queryList(@Valid ${Domain}QueryReq req){
        req.setMemberId(LoginMemberContext.getId());
        PageResp<${Domain}QueryResp> ${domain}QueryRespList = ${domain}Service.queryList(req);
        return new CommonResp<>(${domain}QueryRespList);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id){
        ${domain}Service.delete(id);
        return new CommonResp<>();
    }
}