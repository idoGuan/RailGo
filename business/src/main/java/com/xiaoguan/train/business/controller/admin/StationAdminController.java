package com.xiaoguan.train.business.controller.admin;

import com.xiaoguan.train.business.req.StationQueryReq;
import com.xiaoguan.train.business.req.StationSaveReq;
import com.xiaoguan.train.business.resp.StationQueryResp;
import com.xiaoguan.train.business.service.StationService;
import com.xiaoguan.train.common.resp.CommonResp;
import com.xiaoguan.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/admin/station")
public class StationAdminController {

    @Resource
    private StationService stationService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody StationSaveReq req){
        stationService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<StationQueryResp>> queryList(@Valid StationQueryReq req){
        PageResp<StationQueryResp> stationQueryRespList = stationService.queryList(req);
        return new CommonResp<>(stationQueryRespList);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id){
        stationService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("/query-all")
    public CommonResp<List<StationQueryResp>> queryAll(){
        List<StationQueryResp> stationQueryRespList = stationService.queryAll();
        return new CommonResp<>(stationQueryRespList);
    }
}