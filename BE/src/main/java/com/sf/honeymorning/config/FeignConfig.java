package com.sf.honeymorning.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(basePackages = "com.sf.honeymorning.alarm.client")
@Configuration
public class FeignConfig {
}
