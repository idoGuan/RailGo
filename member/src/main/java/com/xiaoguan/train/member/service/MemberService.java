package com.xiaoguan.train.member.service;

import com.xiaoguan.train.member.mapper.MemberMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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
}
