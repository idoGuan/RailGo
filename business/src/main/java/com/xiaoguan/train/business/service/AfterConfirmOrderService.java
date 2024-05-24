package com.xiaoguan.train.business.service;

import com.xiaoguan.train.business.domain.ConfirmOrder;
import com.xiaoguan.train.business.domain.DailyTrainSeat;
import com.xiaoguan.train.business.domain.DailyTrainTicket;
import com.xiaoguan.train.business.enums.ConfirmOrderStatusEnum;
import com.xiaoguan.train.business.feign.MemberFeign;
import com.xiaoguan.train.business.mapper.ConfirmOrderMapper;
import com.xiaoguan.train.business.mapper.DailyTrainSeatMapper;
import com.xiaoguan.train.business.mapper.cust.DailyTrainTicketMapperCust;
import com.xiaoguan.train.business.req.ConfirmOrderTicketReq;
import com.xiaoguan.train.common.req.MemberTicketReq;
import com.xiaoguan.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AfterConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(AfterConfirmOrderService.class);

    @Resource
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Resource
    private DailyTrainTicketMapperCust dailyTrainTicketMapperCust;

    @Resource
    private MemberFeign memberFeign;

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    /**
     * 选中座位后事务处理:
     *  修改座位表售卖情况sell
     *  修改余票详情表余票
     *  为会员增加购票记录
     *  更新确认订单为成功
     */
    @Transactional
    public void afterDoConfirm(DailyTrainTicket dailyTrainTicket, List<DailyTrainSeat> finalSeatList, List<ConfirmOrderTicketReq> tickets, ConfirmOrder confirmOrder) throws Exception {
        for (int j = 0; j < finalSeatList.size(); j++) {
            DailyTrainSeat dailyTrainSeat = finalSeatList.get(j);
            DailyTrainSeat seatForUpdate = new DailyTrainSeat();
            seatForUpdate.setId(dailyTrainSeat.getId());
            seatForUpdate.setSell(dailyTrainSeat.getSell());
            seatForUpdate.setUpdateTime(new Date());
            dailyTrainSeatMapper.updateByPrimaryKeySelective(seatForUpdate);

            //计算这个站卖出去后，影响哪些站的余票库存
            //参照2-3节 如何保证不超卖、不少卖，还要能承受高并发 10：30左右
            //影响的库存：本次选座之前没卖过票的，和本次购买的区间有交集的区间
            //假设10个站，本次买4~7站
            //原售：0 0 1 0 0 0 0 0 1
            //购买：0 0 0 0 1 1 1 0 0
            //新售：0 0 1 0 1 1 1 0 1
            //影响：X X X 1 1 1 1 1 X
            //如：这里不会影响第一站到第七站之间的售票情况，因为第三站之前已经买过了
            //startIndex = 4
            //endIndex = 7
            //minStartIndex = startIndex - 往前碰到的最后一个0
            //maxStartIndex = endIndex - 1
            //minEndIndex = startIndex + 1
            //maxEndIndex = endIndex + 往后碰到的最后一个0
            Integer startIndex = dailyTrainTicket.getStartIndex();
            Integer endIndex = dailyTrainTicket.getEndIndex();
            char[] chars = seatForUpdate.getSell().toCharArray();
            Integer maxStartIndex = endIndex - 1;
            Integer minEndIndex = startIndex + 1;
            Integer minStartIndex = 0;
            for (int i = startIndex - 1; i >= 0; i--) {
                char c = chars[i];
                if (c == '1') {
                    minEndIndex = i + 1;
                    break;
                }
            }
            LOG.info("影响的出发站区间：" + minStartIndex + "-" + maxStartIndex);
            Integer maxEndIndex = seatForUpdate.getSell().length();
            for (int i = endIndex; i < seatForUpdate.getSell().length(); i++) {
                char c = chars[i];
                if (c == '1') {
                    maxEndIndex = i;
                    break;
                }
            }
            LOG.info("影响的到达站区间：" + minEndIndex + "-" + maxEndIndex);
            dailyTrainTicketMapperCust.updateCountBySell(
                    dailyTrainSeat.getDate(),
                    dailyTrainSeat.getTrainCode(),
                    dailyTrainSeat.getSeatType(),
                    minStartIndex,
                    maxStartIndex,
                    minEndIndex,
                    maxEndIndex);

            // 调用会员服务接口，为会员增加一张车票
            MemberTicketReq memberTicketReq = new MemberTicketReq();
            memberTicketReq.setMemberId(confirmOrder.getMemberId());
            memberTicketReq.setPassengerId(tickets.get(j).getPassengerId());
            memberTicketReq.setPassengerName(tickets.get(j).getPassengerName());
            memberTicketReq.setTrainDate(dailyTrainTicket.getDate());
            memberTicketReq.setTrainCode(dailyTrainTicket.getTrainCode());
            memberTicketReq.setCarriageIndex(dailyTrainSeat.getCarriageIndex());
            memberTicketReq.setSeatRow(dailyTrainSeat.getRow());
            memberTicketReq.setSeatCol(dailyTrainSeat.getCol());
            memberTicketReq.setStartStation(dailyTrainTicket.getStart());
            memberTicketReq.setStartTime(dailyTrainTicket.getStartTime());
            memberTicketReq.setEndStation(dailyTrainTicket.getEnd());
            memberTicketReq.setEndTime(dailyTrainTicket.getEndTime());
            memberTicketReq.setSeatType(dailyTrainSeat.getSeatType());
            CommonResp<Object> commonResp = memberFeign.save(memberTicketReq);
            LOG.info("调用member接口，返回：{}", commonResp);

            // 更新订单状态为成功
            ConfirmOrder confirmOrderForUpdate = new ConfirmOrder();
            confirmOrderForUpdate.setId(confirmOrder.getId());
            confirmOrderForUpdate.setUpdateTime(new Date());
            confirmOrderForUpdate.setStatus(ConfirmOrderStatusEnum.SUCCESS.getCode());
            confirmOrderMapper.updateByPrimaryKeySelective(confirmOrderForUpdate);

            // 模拟调用方出现异常
            // Thread.sleep(10000);
            // if (1 == 1) {
            //     throw new Exception("测试异常");
            // }
        }
    }

}