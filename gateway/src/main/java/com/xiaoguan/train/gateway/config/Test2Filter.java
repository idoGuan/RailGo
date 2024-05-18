//package com.xiaoguan.train.gateway.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
///**
// * ClassName: Test1Filter
// * Package: com.xiaoguan.train.gateway.config
// * Description:
// *
// * @Author 小管不要跑
// * @Create 2024/5/18 10:05
// * @Version 1.0
// */
//@Component
//public class Test2Filter implements GlobalFilter, Ordered {
//
//    private static final Logger LOG = LoggerFactory.getLogger(Test2Filter.class);
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        LOG.info("Test2Filter before");
//        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
//                    LOG.info("Test2Filter after");
//                })
//        );
//    }
//
//    @Override
//    public int getOrder() {
//        return 1;
//    }
//}
