package net.accel_tech.product_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {


    @Bean
    @LoadBalanced // Crucial pour utiliser le nom du service (category-service) au lieu de l'IP
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}