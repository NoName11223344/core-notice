package com.baokim.notification_service.commons.configs.app;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpConfig {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)  // Timeout kết nối
                .readTimeout(30, TimeUnit.SECONDS)     // Timeout đọc dữ liệu
                .writeTimeout(30, TimeUnit.SECONDS)    // Timeout ghi dữ liệu
                .build();
    }
}
