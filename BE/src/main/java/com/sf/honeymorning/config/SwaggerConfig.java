package com.sf.honeymorning.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name}")
    String applicationName;

    @Value("${backend.server.url}")
    String backendServerUrl;

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url(backendServerUrl)) // TODO : 배포를 위함
                .info(new Info().title(applicationName + " API 명세서")
                        .version("0.1")
                        .license(
                                new License()
                                        .name(applicationName)
                                        .url("https://www.honey-morining.com")
                        )
                );
    }
}
