package com.xiaoguan.train.business.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.xiaoguan.train.business.enums.RedisKeyPreEnum;
import com.xiaoguan.train.business.req.ConfirmOrderDoReq;
import com.xiaoguan.train.common.exception.BusinessException;
import com.xiaoguan.train.common.exception.BusinessExceptionEnum;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * ClassName: BeforeOrderService
 * Package: com.xiaoguan.train.business.service
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/30 14:47
 * @Version 1.0
 */
public class BeforeConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(BeforeConfirmOrderService.class);

    @Resource
    private SkTokenService skTokenService;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @SentinelResource(value = "beforeDoConfirm", blockHandler = "beforeDoConfirmBlock")
    public void beforeDoConfirm(ConfirmOrderDoReq req){

        //校验令牌余量
        boolean validSkToken = skTokenService.validSkToken(req.getDate(), req.getTrainCode(), req.getMemberId());
        if(validSkToken){
            LOG.info("令牌校验通过");
        }
        else{
            LOG.info("令牌校验不通过");
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
        }
        //购票
        String lockKey = RedisKeyPreEnum.CONFIRM_ORDER + "-" + DateUtil.formatDate(req.getDate()) + "-" + req.getTrainCode();

        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);

        if(setIfAbsent){
            LOG.info("恭喜，抢票成功");
        }else{
            //只是没抢到锁，并不知道票买完了没，所以提示稍后重试
            LOG.info("很遗憾，没抢到锁");
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
        }
    }

    /**
     * 降级方法，需包含限流方法的所有参数和BlockException参数
     * @param req
     * @param e
     */
    public void beforeDoConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
        LOG.info("购票请求被限流：{}", req);
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }
}
