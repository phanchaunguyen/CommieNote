package com.CommieNote.masternote.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE) // ÉP CHẠY ĐẦU TIÊN
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Cấp phép thông hành
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("https://*.up.railway.app"); // Chấp nhận mọi domain từ Railway
        config.addAllowedOriginPattern("http://localhost:5173");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}