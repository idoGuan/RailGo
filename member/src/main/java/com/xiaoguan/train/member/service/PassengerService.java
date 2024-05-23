package com.xiaoguan.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoguan.train.common.context.LoginMemberContext;
import com.xiaoguan.train.common.resp.PageResp;
import com.xiaoguan.train.common.util.SnowUtil;
import com.xiaoguan.train.member.domain.Passenger;
import com.xiaoguan.train.member.domain.PassengerExample;
import com.xiaoguan.train.member.mapper.PassengerMapper;
import com.xiaoguan.train.member.req.PassengerQueryReq;
import com.xiaoguan.train.member.req.PassengerSaveReq;
import com.xiaoguan.train.member.resp.PassengerQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: PassengerService
 * Package: com.xiaoguan.train.member.service
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/18 12:57
 * @Version 1.0
 */
@Service
public class PassengerService {

    public static final Logger LOG = LoggerFactory.getLogger(PassengerService.class);

    @Resource
    private PassengerMapper passengerMapper;

    //这里不需要返回值，因为在后续界面操作时，保存后界面会刷新列表，查询数据，不需要返回保存成功后的数据
    //因此这里新增数据只需要将数据保存到数据库就行（有返回值也行，新建一个PassengerSaveResp）
    public void save(PassengerSaveReq req) {
        DateTime now = DateTime.now();
        Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
        if (ObjUtil.isNull(passenger.getId())) {
            passenger.setMemberId(LoginMemberContext.getId());
            passenger.setId(SnowUtil.getSnowflakeNextId());
            passenger.setCreateTime(now);
            passenger.setUpdateTime(now);
            passengerMapper.insert(passenger);
        } else {
            passenger.setUpdateTime(now);
            passengerMapper.updateByPrimaryKey(passenger);
        }
    }

    public PageResp<PassengerQueryResp> queryList(PassengerQueryReq req) {
        PassengerExample passengerExample = new PassengerExample();
        passengerExample.setOrderByClause("id desc");
        //如果有多个条件变量的话，要在同一个criteria上面添加and条件，否则的话只有最后的criteria条件生效
        PassengerExample.Criteria criteria = passengerExample.createCriteria();
        if (ObjUtil.isNotNull(req.getMemberId())) {
            criteria.andMemberIdEqualTo(req.getMemberId());
        }
        LOG.info("查询页码，{}", req.getPage());
        LOG.info("每页条数，{}", req.getSize());
        //分页代码尽量与查询操作放在一起，防止两者中间出现别的查询操作，出现错误
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Passenger> passengerList = passengerMapper.selectByExample(passengerExample);

        PageInfo<Passenger> pageInfo = new PageInfo<>(passengerList);
        LOG.info("总行数，{}", pageInfo.getTotal());
        LOG.info("总页数，{}", pageInfo.getPages());


        List<PassengerQueryResp> list = BeanUtil.copyToList(passengerList, PassengerQueryResp.class);
        PageResp<PassengerQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;

    }

    public void delete(Long id) {
        passengerMapper.deleteByPrimaryKey(id);
    }

    /**
     * 查询我的所有乘客
     */
    public List<PassengerQueryResp> queryMine(){
        PassengerExample passengerExample = new PassengerExample();
        passengerExample.setOrderByClause("name asc");
        PassengerExample.Criteria criteria = passengerExample.createCriteria();
        criteria.andMemberIdEqualTo(LoginMemberContext.getId());
        List<Passenger> passengerList = passengerMapper.selectByExample(passengerExample);
        return BeanUtil.copyToList(passengerList, PassengerQueryResp.class);

    }
}