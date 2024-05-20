package com.xiaoguan.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoguan.train.common.resp.PageResp;
import com.xiaoguan.train.common.util.SnowUtil;
import com.xiaoguan.train.business.domain.Station;
import com.xiaoguan.train.business.domain.StationExample;
import com.xiaoguan.train.business.mapper.StationMapper;
import com.xiaoguan.train.business.req.StationQueryReq;
import com.xiaoguan.train.business.req.StationSaveReq;
import com.xiaoguan.train.business.resp.StationQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: StationService
 * Package: com.xiaoguan.train.business.service
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/18 12:57
 * @Version 1.0
 */
@Service
public class StationService {

    public static final Logger LOG = LoggerFactory.getLogger(StationService.class);

    @Resource
    private StationMapper stationMapper;

    //这里不需要返回值，因为在后续界面操作时，保存后界面会刷新列表，查询数据，不需要返回保存成功后的数据
    //因此这里新增数据只需要将数据保存到数据库就行（有返回值也行，新建一个StationSaveResp）
    public void save(StationSaveReq req) {
        DateTime now = DateTime.now();
        Station station = BeanUtil.copyProperties(req, Station.class);
        if (ObjUtil.isNull(station.getId())) {
            station.setId(SnowUtil.getSnowflakeNextId());
            station.setCreateTime(now);
            station.setUpdateTime(now);
            stationMapper.insert(station);
        } else {
            station.setUpdateTime(now);
            stationMapper.updateByPrimaryKey(station);
        }
    }

    public PageResp<StationQueryResp> queryList(StationQueryReq req) {
        StationExample stationExample = new StationExample();
        //如果有多个条件变量的话，要在同一个criteria上面添加and条件，否则的话只有最后的criteria条件生效
        StationExample.Criteria criteria = stationExample.createCriteria();

        LOG.info("查询页码，{}", req.getPage());
        LOG.info("每页条数，{}", req.getSize());
        //分页代码尽量与查询操作放在一起，防止两者中间出现别的查询操作，出现错误
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Station> stationList = stationMapper.selectByExample(stationExample);

        PageInfo<Station> pageInfo = new PageInfo<>(stationList);
        LOG.info("总行数，{}", pageInfo.getTotal());
        LOG.info("总页数，{}", pageInfo.getPages());


        List<StationQueryResp> list = BeanUtil.copyToList(stationList, StationQueryResp.class);
        PageResp<StationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;

    }

    public void delete(Long id) {
        stationMapper.deleteByPrimaryKey(id);
    }
}