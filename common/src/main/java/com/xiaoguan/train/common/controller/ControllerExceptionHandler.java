package com.xiaoguan.train.common.controller;

/**
 * ClassName: ControllerExceptionHandler
 * Package: com.xiaoguan.train.common.controller
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/17 8:07
 * @Version 1.0
 */

import com.xiaoguan.train.common.resp.CommonResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理、数据预处理等
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    /**
     * 所有异常统一处理
     * @param e 异常
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CommonResp exceptionHandler(Exception e) throws Exception{
        CommonResp commonResp = new CommonResp<>();
        LOG.error("系统异常：", e);
        commonResp.setSuccess(false);
//        commonResp.setMessage("系统出现异常，请联系管理员");
        commonResp.setMessage(e.getMessage());
        return commonResp;
    }

}
