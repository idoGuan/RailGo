package com.xiaoguan.train.common.exceprion;

/**
 * ClassName: BusinessException
 * Package: com.xiaoguan.train.common.exceprion
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/17 8:44
 * @Version 1.0
 */
public class BusinessException extends RuntimeException{
    private BusinessExceptionEnum e;

    public BusinessException(BusinessExceptionEnum e) {
        this.e = e;
    }

    public BusinessExceptionEnum getE() {
        return e;
    }

    public void setE(BusinessExceptionEnum e) {
        this.e = e;
    }

    /**
     * 对于自己定义的异常类，不写入堆栈信息，提高性能
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
