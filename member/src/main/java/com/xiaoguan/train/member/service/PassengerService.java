package com.xiaoguan.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjUtil;
import com.github.pagehelper.PageHelper;
import com.xiaoguan.train.common.context.LoginMemberContext;
import com.xiaoguan.train.common.util.SnowUtil;
import com.xiaoguan.train.member.domain.Passenger;
import com.xiaoguan.train.member.domain.PassengerExample;
import com.xiaoguan.train.member.mapper.PassengerMapper;
import com.xiaoguan.train.member.req.PassengerQueryReq;
import com.xiaoguan.train.member.req.PassengerSaveReq;
import com.xiaoguan.train.member.resp.PassengerQueryResp;
import jakarta.annotation.Resource;
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

    @Resource
    private PassengerMapper passengerMapper;

    //这里不需要返回值，因为在后续界面操作时，保存后界面会刷新列表，查询数据，不需要返回保存成功后的数据
    //因此这里新增数据只需要将数据保存到数据库就行（有返回值也行，新建一个PassengerSaveResp）
    public void save(PassengerSaveReq req){
        DateTime now = DateTime.now();
        Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
        passenger.setMemberId(LoginMemberContext.getId());
        passenger.setId(SnowUtil.getSnowflakeNextId());
        passenger.setCreateTime(now);
        passenger.setUpdateTime(now);
        passengerMapper.insert(passenger);
    }

    public List<PassengerQueryResp> queryList(PassengerQueryReq req){
        PassengerExample passengerExample = new PassengerExample();
        //如果有多个条件变量的话，要在同一个criteria上面添加and条件，否则的话只有最后的criteria条件生效
        PassengerExample.Criteria criteria = passengerExample.createCriteria();
        if(ObjUtil.isNotNull(req.getMemberId())){
            criteria.andMemberIdEqualTo(req.getMemberId());
        }
        //分页代码尽量与查询操作放在一起，防止两者中间出现别的查询操作，出现错误
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Passenger> passengerList = passengerMapper.selectByExample(passengerExample);
        return BeanUtil.copyToList(passengerList, PassengerQueryResp.class);
    }
}
