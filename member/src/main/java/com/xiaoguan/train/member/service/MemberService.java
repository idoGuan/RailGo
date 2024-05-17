package com.xiaoguan.train.member.service;

import cn.hutool.core.collection.CollUtil;
import com.xiaoguan.train.common.exceprion.BusinessException;
import com.xiaoguan.train.common.exceprion.BusinessExceptionEnum;
import com.xiaoguan.train.common.util.SnowUtil;
import com.xiaoguan.train.member.domain.Member;
import com.xiaoguan.train.member.domain.MemberExample;
import com.xiaoguan.train.member.mapper.MemberMapper;
import com.xiaoguan.train.member.req.MemberRequestReq;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: MemberService
 * Package: com.xiaoguan.train.member.service
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/16 17:56
 * @Version 1.0
 */
@Service
public class MemberService {
    @Resource
    private MemberMapper memberMapper;

    public int count(){
        return Math.toIntExact(memberMapper.countByExample(null));
    }

    public long register(MemberRequestReq req){
        //1.创建一个条件
        String mobile = req.getMobile();
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        //2.根据条件进行查询
        List<Member> list = memberMapper.selectByExample(memberExample);
        if(CollUtil.isNotEmpty(list)){
            //带验证码的注册可以用这种方式，有验证码，说明手机号是本人用，原来注册过的，就不需要保存了，直接数据库返回。
            // 这个接口既可以是注册，也可以是登陆
//            return list.get(0).getId();
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }
        Member member = new Member();
        member.setId(SnowUtil.getSnowflakeNextId());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }
}
