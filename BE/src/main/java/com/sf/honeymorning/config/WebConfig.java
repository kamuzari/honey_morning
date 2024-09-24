package com.sf.honeymorning.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowedOrigins.frontend}")
    private String allowedOriginsFrontend;

    @Value("${cors.allowedOrigins.ai.briefing}")
    private String allowedOriginsBriefing;

    @Value("${cors.allowedOrigins.ai.quiz}")
    private String allowedOriginsQuiz;

    @Value("${cors.allowedOrigins.ai.music}")
    private String allowedOriginsMusic;

    @Value("${cors.allowedOrigins.ai.topic}")
    private String allowedOriginsTopic;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOriginsFrontend,
                        allowedOriginsBriefing,
                        allowedOriginsQuiz,
                        allowedOriginsMusic,
                        allowedOriginsTopic)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Set-Cookie")
                .allowCredentials(true);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
