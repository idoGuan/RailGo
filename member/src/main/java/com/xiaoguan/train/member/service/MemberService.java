package com.xiaoguan.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.jwt.JWTUtil;
import com.xiaoguan.train.common.exceprion.BusinessException;
import com.xiaoguan.train.common.exceprion.BusinessExceptionEnum;
import com.xiaoguan.train.common.util.SnowUtil;
import com.xiaoguan.train.member.domain.Member;
import com.xiaoguan.train.member.domain.MemberExample;
import com.xiaoguan.train.member.mapper.MemberMapper;
import com.xiaoguan.train.member.req.MemberLoginReq;
import com.xiaoguan.train.member.req.MemberRequestReq;
import com.xiaoguan.train.member.req.MemberSendCodeReq;
import com.xiaoguan.train.member.resp.MemberLoginResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

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

    private static final Logger LOG = LoggerFactory.getLogger(MemberService.class);

    @Resource
    private MemberMapper memberMapper;

    public int count(){
        return Math.toIntExact(memberMapper.countByExample(null));
    }

    public long register(MemberRequestReq req){
        //1.创建一个条件
        String mobile = req.getMobile();
        Member memberDB = selectByMobile(mobile);
        if(ObjectUtil.isNotNull(memberDB)){
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

    public void sendCode(MemberSendCodeReq req){
        //1. 创建一个条件
        String mobile = req.getMobile();
        Member memberDB = selectByMobile(mobile);
        //3. 如果号码不存在，则插入一条记录
        if(ObjectUtil.isNull(memberDB)){
            LOG.info("手机号不存在，插入一条记录");
            Member member = new Member();
            member.setId(SnowUtil.getSnowflakeNextId());
            member.setMobile(mobile);
            memberMapper.insert(member);
        }else{
            LOG.info("手机号存在，不插入记录");
        }

        //4. 生成验证码
//        String code = RandomUtil.randomString(4);
        //这里为了方便测试，将短信验证码固定为8888
        String code = "8888";
        LOG.info("生成短信验证码：{}", code);

        //Todo 保存短信记录表：手机号、短信验证码、有效期、是否已使用、业务类型、发送时间、使用时间
        LOG.info("保存短信记录表");
        //Todo 对接短信通道，发送短信
        LOG.info("对接短信通道");

    }

    public MemberLoginResp login(MemberLoginReq req){
        String mobile = req.getMobile();
        String code = req.getCode();
        //创建一个条件
        Member memberDB = selectByMobile(mobile);
        //如果号码不存在，则插抛出异常
        if(ObjectUtil.isNull(memberDB)){
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);
        }

        // 校验短信验证码
        if(!"8888".equals(code)){
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_CODE_ERROR);
        }

        MemberLoginResp memberLoginResp = BeanUtil.copyProperties(memberDB, MemberLoginResp.class);
        Map<String, Object> map = BeanUtil.beanToMap(memberLoginResp);
        String key = "Xiaoguan12306";
        String token = JWTUtil.createToken(map, key.getBytes(StandardCharsets.UTF_8));
        memberLoginResp.setToken(token);
        return memberLoginResp;
    }

    private Member selectByMobile(String mobile) {
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        //根据条件进行查询
        List<Member> list = memberMapper.selectByExample(memberExample);
        if(CollUtil.isEmpty(list)){
            return null;
        }
        
        return list.get(0);
    }
}
