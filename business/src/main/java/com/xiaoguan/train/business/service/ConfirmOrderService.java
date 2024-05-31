package com.xiaoguan.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoguan.train.business.domain.*;
import com.xiaoguan.train.business.dto.ConfirmOrderMQDto;
import com.xiaoguan.train.business.enums.ConfirmOrderStatusEnum;
import com.xiaoguan.train.business.enums.RedisKeyPreEnum;
import com.xiaoguan.train.business.enums.SeatColEnum;
import com.xiaoguan.train.business.enums.SeatTypeEnum;
import com.xiaoguan.train.business.mapper.ConfirmOrderMapper;
import com.xiaoguan.train.business.req.ConfirmOrderDoReq;
import com.xiaoguan.train.business.req.ConfirmOrderQueryReq;
import com.xiaoguan.train.business.req.ConfirmOrderTicketReq;
import com.xiaoguan.train.business.resp.ConfirmOrderQueryResp;
import com.xiaoguan.train.common.exception.BusinessException;
import com.xiaoguan.train.common.exception.BusinessExceptionEnum;
import com.xiaoguan.train.common.resp.PageResp;
import com.xiaoguan.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @Resource
    private AfterConfirmOrderService afterConfirmOrderService;

    @Autowired
    private StringRedisTemplate redisTemplate;
//    @Autowired
//    private RedissonClient redissonClient;

    @Resource
    private SkTokenService skTokenService;

    public void save(ConfirmOrderDoReq req) {
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        if (ObjectUtil.isNull(confirmOrder.getId())) {
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);
        } else {
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateByPrimaryKey(confirmOrder);
        }
    }

    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req) {
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        confirmOrderExample.setOrderByClause("id desc");
        ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<ConfirmOrder> confirmOrderList = confirmOrderMapper.selectByExample(confirmOrderExample);

        PageInfo<ConfirmOrder> pageInfo = new PageInfo<>(confirmOrderList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<ConfirmOrderQueryResp> list = BeanUtil.copyToList(confirmOrderList, ConfirmOrderQueryResp.class);

        PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        confirmOrderMapper.deleteByPrimaryKey(id);
    }

    @SentinelResource(value = "doConfirm", blockHandler = "doConfirmBlock")
    public void doConfirm(ConfirmOrderMQDto dto){

//        //校验令牌余量
//        boolean validSkToken = skTokenService.validSkToken(req.getDate(), req.getTrainCode(), req.getMemberId());
//        if(validSkToken){
//            LOG.info("令牌校验通过");
//        }
//        else{
//            LOG.info("令牌校验不通过");
//            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
//        }
        //购票
        String lockKey = RedisKeyPreEnum.CONFIRM_ORDER + "-" + DateUtil.formatDate(dto.getDate()) + "-" + dto.getTrainCode();

        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);

        if(setIfAbsent){
            LOG.info("恭喜，抢票成功");
        }else{
            //只是没抢到锁，并不知道票买完了没，所以提示稍后重试
//            LOG.info("很遗憾，没抢到锁");
//            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);

            //因为当线程走到抢票逻辑的时候，说明订单信息已经被保存到数据库中
            //不管哪个线程抢到了锁，都会去数据库中查询所有该车次的订单信息并且完成下单操作
            LOG.info("没抢到锁，有其它消费线程正在出票，不做任何处理");
            return;
        }
//        RLock lock = null;
        try {
            //使用redisson，自带看门狗
//            lock = redissonClient.getLock(lockKey);
             /**
               waitTime – the maximum time to acquire the lock 等待获取锁时间(最大尝试获得锁的时间)，超时返回false
               leaseTime – lease time 锁时长，即n秒后自动释放锁
               time unit – time unit 时间单位
              */
             // boolean tryLock = lock.tryLock(30, 10, TimeUnit.SECONDS); // 不带看门狗
//             boolean tryLock = lock.tryLock(0, TimeUnit.SECONDS); // 带看门狗
//             if (tryLock) {
//                 LOG.info("恭喜，抢到锁了！");
//                 // 可以把下面这段放开，只用一个线程来测试，看看redisson的看门狗效果
//                 // for (int i = 0; i < 30; i++) {
//                 //     Long expire = redisTemplate.opsForValue().getOperations().getExpire(lockKey);
//                 //     LOG.info("锁过期时间还有：{}", expire);
//                 //     Thread.sleep(1000);
//                 // }
//             } else {
//                 // 只是没抢到锁，并不知道票抢完了没，所以提示稍候再试
//                 LOG.info("很遗憾，没抢到锁");
//                 throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
//             }

            while (true) {
                // 取确认订单表的记录，同日期车次，状态是I，分页处理，每次取N条
                ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
                confirmOrderExample.setOrderByClause("id asc");
                ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();
                criteria.andDateEqualTo(dto.getDate())
                        .andTrainCodeEqualTo(dto.getTrainCode())
                        .andStatusEqualTo(ConfirmOrderStatusEnum.INIT.getCode());
                PageHelper.startPage(1, 5);
                List<ConfirmOrder> list = confirmOrderMapper.selectByExampleWithBLOBs(confirmOrderExample);

                if (CollUtil.isEmpty(list)) {
                    LOG.info("没有需要处理的订单，结束循环");
                    break;
                } else {
                    LOG.info("本次处理{}条订单", list.size());
                }

                // 一条一条的卖
                list.forEach(confirmOrder -> {
                    try {
                        sell(confirmOrder);
                    } catch (BusinessException e) {
                        if (e.getE().equals(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR)) {
                            LOG.info("本订单余票不足，继续售卖下一个订单");
                            confirmOrder.setStatus(ConfirmOrderStatusEnum.EMPTY.getCode());
                            updateStatus(confirmOrder);
                        } else {
                            throw e;
                        }
                    }
                });
            }
//        } catch (InterruptedException e) {
//            LOG.error("购票异常", e);
        } finally {
            LOG.info("购票流程结束，释放锁！lockKey:{}", lockKey);
            redisTemplate.delete(lockKey);
//            if(null != lock && lock.isHeldByCurrentThread()){
//                lock.unlock();
//            }
        }

    }

    /**
     * 更新状态
     */
    public void updateStatus(ConfirmOrder confirmOrder){
        ConfirmOrder confirmOrderForUpdate = new ConfirmOrder();
        confirmOrderForUpdate.setId(confirmOrder.getId());
        confirmOrderForUpdate.setUpdateTime(new Date());
        confirmOrderForUpdate.setStatus(confirmOrder.getStatus());
        confirmOrderMapper.updateByPrimaryKey(confirmOrderForUpdate);
    }

    private void sell(ConfirmOrder confirmOrder) {
        // 构造ConfirmOrderDoReq
        ConfirmOrderDoReq req = new ConfirmOrderDoReq();
        req.setMemberId(confirmOrder.getMemberId());
        req.setDate(confirmOrder.getDate());
        req.setTrainCode(confirmOrder.getTrainCode());
        req.setStart(confirmOrder.getStart());
        req.setEnd(confirmOrder.getEnd());
        req.setDailyTrainTicketId(confirmOrder.getDailyTrainTicketId());
        req.setTickets(JSON.parseArray(confirmOrder.getTickets(), ConfirmOrderTicketReq.class));
        req.setImageCode("");
        req.setImageCodeToken("");
        req.setLogId("");
        //省略业务数据校验，如：车次是否存在，余票是否存在，车次是否在有效期内，ticket条数>0，同乘客同车次是否已经买过

        //将订单设置成处理中，避免重复处理
        LOG.info("将确认订单更新成处理中，避免重复处理，confirmOrder.id：{}", confirmOrder.getId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.PENDING.getCode());
        updateStatus(confirmOrder);


        DateTime now = DateTime.now();
        List<ConfirmOrderTicketReq> tickets = req.getTickets();
        String trainCode = req.getTrainCode();
        String start = req.getStart();
        String end = req.getEnd();
        Date date = req.getDate();
//            ConfirmOrder confirmOrder = new ConfirmOrder();
//            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
//            confirmOrder.setMemberId(req.getMemberId());
//
//            confirmOrder.setDate(date);
//            confirmOrder.setTrainCode(trainCode);
//            confirmOrder.setStart(start);
//            confirmOrder.setEnd(end);
//            confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
//            confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
//            confirmOrder.setCreateTime(now);
//            confirmOrder.setUpdateTime(now);
//            confirmOrder.setTickets(JSON.toJSONString(tickets));
//            confirmOrderMapper.insert(confirmOrder);
        //从数据库中查询订单
//        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
//        confirmOrderExample.setOrderByClause("id asc");
//        ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();
//        criteria.andDateEqualTo(req.getDate())
//                .andTrainCodeEqualTo(req.getTrainCode())
//                .andMemberIdEqualTo(req.getMemberId())
//                .andStatusEqualTo(ConfirmOrderStatusEnum.INIT.getCode());
//        List<ConfirmOrder> confirmOrderList = confirmOrderMapper.selectByExampleWithBLOBs(confirmOrderExample);
//        ConfirmOrder confirmOrder;
//        if(CollUtil.isEmpty(confirmOrderList)){
//            LOG.info("找不到订单信息，结束");
//            return true;
//        }
//        else{
//            LOG.info("本次处理{}条确认订单", confirmOrderList.size());
//            confirmOrder = confirmOrderList.get(0);
//        }

        //查出余票记录，需要得到真实的库存
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        LOG.info("查出余票记录：{}", dailyTrainTicket);

        //扣减余票数量，并判断余票是否足够（这里是预扣减，在Java类里扣减，不能直接更新到数据库)
        reduceTickets(req, dailyTrainTicket);

        //最终地选座结果
        List<DailyTrainSeat> finalSeatList = new ArrayList<>();
        //计算相对第一个座位的偏移值
        //比如选择的是C1,D2，则偏移值是[0,5]
        //比如选择的是A1,B1,C1，则偏移值是[0,1,2]
        ConfirmOrderTicketReq ticketReq0 = tickets.get(0);
        //判断用户是否选座
        if(!StrUtil.isBlank(ticketReq0.getSeat())){
            LOG.info("本次购票有选座");
            //查出本次选座的座位类型都有哪些列，用于计算所选座位与第一个座位的偏移值
            //如果选座了的话，那么所有的座位的类型应该都是一样的，比如说全是一等座，因此只需要查询第一张票的座位类型即可
            //SeatColEnum: 例：YDZ_A YDZ_C YDZ_D YDZ_F
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(ticketReq0.getSeatTypeCode());

            //组成和前端两排座位一样的列表，用于做参照的座位列表，例：referSeatList = {A1, C1, D1, F1}
            List<String> referSeatList = new ArrayList<>();
            for(int i = 1; i <= 2; i++){
                for (SeatColEnum seatColEnum : colEnumList) {
                    referSeatList.add(seatColEnum.getCode() + i);
                }
            }
            LOG.info("用于作参照的两排座位：{}", referSeatList);

            //绝对偏移值，即：在参照座位列表中的位置
            List<Integer> absoluteOffsetList = new ArrayList<>();
            //相对偏移值，即：与第一个座位偏移的距离
            List<Integer> offsetList = new ArrayList<>();
            for (ConfirmOrderTicketReq ticket : tickets) {
                int index = referSeatList.indexOf(ticket.getSeat());
                absoluteOffsetList.add(index);
            }
            LOG.info("计算得到所有座位的绝对偏移值：{}", absoluteOffsetList);
            for (Integer offset : absoluteOffsetList) {
                offsetList.add(offset - absoluteOffsetList.get(0));
            }
            LOG.info("计算得到所有座位与第一个座位的相对偏移值：{}", offsetList);

            getSeat(finalSeatList,
                    date,
                    trainCode,
                    ticketReq0.getSeatTypeCode(),
                    ticketReq0.getSeat().split("")[0],//从A1得到A
                    offsetList,
                    dailyTrainTicket.getStartIndex(),
                    dailyTrainTicket.getEndIndex()
            );
        }else{
            LOG.info("本次购票没有选座");
            for (ConfirmOrderTicketReq ticket : tickets) {
                getSeat(finalSeatList,
                        date, trainCode,
                        ticket.getSeatTypeCode(),
                        null,
                        null,
                        dailyTrainTicket.getStartIndex(),
                        dailyTrainTicket.getEndIndex());
            }
        }

        LOG.info("最终的选座：{}", finalSeatList);


        // 选中座位后事务处理:

        //修改座位表售卖情况sell
        //修改余票详情表余票
        //为会员增加购票记录
        //更新确认订单为成功
        try {
            afterConfirmOrderService.afterDoConfirm(dailyTrainTicket, finalSeatList, tickets, confirmOrder);
        } catch (Exception e) {
            LOG.error("保存购票信息失败", e);
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
        }
    }

    private void getSeat(List<DailyTrainSeat> finalSeatList,
                         Date date, String trainCode,
                         String seatType, String column,
                         List<Integer> offsetList,
                         Integer startIndex,
                         Integer endIndex) {
        List<DailyTrainSeat> getSeatList = new ArrayList<>();
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectByTrainCode(date, trainCode, seatType);
        LOG.info("共查出{}个符合条件的车厢", carriageList.size());

        //一个车厢一个车厢的获取座位数据
        for (DailyTrainCarriage carriage : carriageList) {
            LOG.info("开始从车厢{}选座", carriage.getIndex());
            //更换车厢之后getSeatList的数据也也需要清空
            getSeatList = new ArrayList<>();
            List<DailyTrainSeat> carriageSeatList = dailyTrainSeatService.selectByCarriage(date, trainCode, carriage.getIndex());
            LOG.info("车厢{}的座位数: {}", carriage.getIndex(), carriageSeatList.size());
            for (DailyTrainSeat dailyTrainSeat : carriageSeatList) {
                String col = dailyTrainSeat.getCol();
                Integer seatIndex = dailyTrainSeat.getCarriageSeatIndex();

                boolean alreadyChooseFlag = false;
                for (DailyTrainSeat trainSeat : finalSeatList) {
                    //这里要根据id是否相等来判断当前座位是否被选中
                    //不能判断对象，因为选中后，sell信息会被更新，对象信息变了
                    if(trainSeat.getId().equals(dailyTrainSeat.getId())){
                        alreadyChooseFlag = true;
                        break;
                    }
                }
                if(alreadyChooseFlag){
                    LOG.info("座位{}被选中过，不能重复选中，继续判断下一个座位", seatIndex);
                    break;
                }

                //判断column，有值的话要对比列号
                if(!StrUtil.isNotBlank(column)){
                    LOG.info("无选座");
                }
                else{

                    if(!column.equals(col)){
                        LOG.info("座位{}列值不对，继续判断下一个座位，当前列值：{}，目标列值：{}",
                                seatIndex, col, column);
                        continue;
                    }
                }

                boolean isChoose = calSell(dailyTrainSeat, startIndex, endIndex);
                if(isChoose){
                    LOG.info("选中座位");
                    getSeatList.add(dailyTrainSeat);
                }else{
                    continue;
                }

                //根据offset选剩下的座位
                boolean isGetAllOffsetSeat = true;
                if(CollUtil.isNotEmpty(offsetList)){
                    LOG.info("有偏移值：{}，校验偏移的座位是否可选", offsetList);
                    //从索引一开始，索引0就是当前已选中的票
                    for (int i = 1; i < offsetList.size(); i++) {
                        Integer offset = offsetList.get(i);
                        int nextIndex = offset + seatIndex;

                        //如果有选座的话，一定是在同一节车厢
                        if(nextIndex >= carriageSeatList.size()){
                            LOG.info("座位{}不可选，偏移后的索引超出了这个车厢的座位数", nextIndex);
                            isGetAllOffsetSeat = false;
                            break;
                        }
                        //因为这里的索引是从0开始的，例：一号座位的索引是0，二号座位的索引是1
                        DailyTrainSeat nextSeat = carriageSeatList.get(nextIndex - 1);
                        boolean isNextChoose = calSell(nextSeat, startIndex, endIndex);
                        if(isNextChoose){
                            LOG.info("座位{}被选中", nextSeat.getCarriageSeatIndex());
                            getSeatList.add(nextSeat);
                        }else{
                            LOG.info("座位{}不可选中", nextSeat.getCarriageSeatIndex());
                            isGetAllOffsetSeat = false;
                            break;
                        }
                    }
                }
                if(!isGetAllOffsetSeat){
                    getSeatList = new ArrayList<>();
                    continue;
                }

                //保存选好的座位
                finalSeatList.addAll(getSeatList);
                return;
            }
        }
    }

    /**
     * 计算某座位在区间内是否可卖
     * 例：sell=10001，本次购买区间站1~4，则区间已售000
     * 全部是0，表示这个区间可买；只要有1，就表示区间内已售过票
     *
     * 选中后，要计算购票后的sell，比如原来是10001，本次购买区间站1~4
     * 方案：构造本次购票造成的售卖信息01110，和原sell 10001按位或，最终得到11111
     */
    private boolean calSell(DailyTrainSeat dailyTrainSeat, Integer startIndex, Integer endIndex){
        String sell = dailyTrainSeat.getSell();
        String sellPart = sell.substring(startIndex, endIndex);
        if(Integer.parseInt(sellPart) > 0){
            LOG.info("座位{}在本次车站区间{}~{}已售过票，不可选中该座位", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            return false;
        }else{
            LOG.info("座位{}在本次车站区间{}~{}未售过票，可以选中该座位", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            // 111
            String curSell = sellPart.replace('0', '1');
            //0111 (表示将curSell变为长度为endIndex的字符串，长度不够前面补0)
            curSell= StrUtil.fillBefore(curSell, '0', endIndex);
            //01110
            curSell= StrUtil.fillAfter(curSell, '0', sell.length());

            //当前区间售票信息curSell与库里的已售信息sell按位或，即可得到该座位卖出此票后的售票详情
            int newSellInt = NumberUtil.binaryToInt(curSell) | NumberUtil.binaryToInt(sell);
            String newSell = NumberUtil.getBinaryStr(newSellInt);
            //数字转成二进制前面的0可能丢失
            newSell= StrUtil.fillBefore(newSell, '0', sell.length());
            LOG.info("座位{}被选中，原售票信息：{}，车站区间：{}~{}，即：{}，最终售票信息：{}",
                    dailyTrainSeat.getCarriageIndex(),
                    sell, startIndex, endIndex, curSell, newSell);
            dailyTrainSeat.setSell(newSell);
            return true;
        }
    }

    private void reduceTickets(ConfirmOrderDoReq req, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketReq ticket : req.getTickets()) {
            String seatTypeCode = ticket.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
            switch (seatTypeEnum){
                case YDZ -> {
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if(countLeft < 0){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ -> {
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if(countLeft < 0){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setEdz(countLeft);
                }
                case RW -> {
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if(countLeft < 0){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setRw(countLeft);
                }
                case YW -> {
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if(countLeft < 0){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYw(countLeft);
                }
            }
        }
    }

    /**
     * 降级方法，需包含限流方法的所有参数和BlockException参数
     * @param req
     * @param e
     */
    public void doConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
        LOG.info("购票请求被限流：{}", req);
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }
}