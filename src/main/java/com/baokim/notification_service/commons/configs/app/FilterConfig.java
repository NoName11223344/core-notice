package com.baokim.notification_service.commons.configs.app;

import com.baokim.notification_service.commons.filters.SignFilter;
import com.baokim.notification_service.features.sms.filters.SmsLimitFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final SignFilter signFilter;
    private final SmsLimitFilter smsLimitFilter;

    @Bean
    public FilterRegistrationBean<SignFilter> checkSignFilter() {
        FilterRegistrationBean<SignFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(signFilter);
        registrationBean.addUrlPatterns("/api/sms/brand-name/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<SmsLimitFilter> checkSmsLimitFilter() {
        FilterRegistrationBean<SmsLimitFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(smsLimitFilter);
        registrationBean.addUrlPatterns("/api/sms/brand-name/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }

}