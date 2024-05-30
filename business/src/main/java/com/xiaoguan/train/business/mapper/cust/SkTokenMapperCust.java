package com.xiaoguan.train.business.mapper.cust;

import java.util.Date;

/**
 * ClassName: SkTokenMapperCust
 * Package: com.xiaoguan.train.business.mapper.cust
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/29 14:13
 * @Version 1.0
 */
public interface SkTokenMapperCust {

    int decrease(Date date, String trainCode, int decreaseCount);
}
