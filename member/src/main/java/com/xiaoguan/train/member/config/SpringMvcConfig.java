package com.xiaoguan.train.member.config;

import com.xiaoguan.train.common.interceptor.LogInterceptor;
import com.xiaoguan.train.common.interceptor.MemberInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ClassName: SpringMvcConfig
 * Package: com.xiaoguan.train.member.config
 * Description:
 *
 * @Author 小管不要跑
 * @Create 2024/5/18 15:04
 * @Version 1.0
 */
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Resource
    private MemberInterceptor memberInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor());
        registry.addInterceptor(memberInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/hello",
                        "/send-code",
                        "/login"
                );
    }
}
