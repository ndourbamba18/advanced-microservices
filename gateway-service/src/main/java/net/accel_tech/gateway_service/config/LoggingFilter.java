package net.accel_tech.gateway_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. Actions AVANT l'appel au microservice (Pre-filter)
        long startTime = System.currentTimeMillis();
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod().name();

        log.info("Requête entrante : {} {} - [Gateway]", method, path);

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // 2. Actions APRÈS l'appel (Post-filter)
            long stopTime = System.currentTimeMillis();
            long duration = stopTime - startTime;
            int statusCode = exchange.getResponse().getStatusCode().value();

            log.info("Réponse sortante : {} {} | Statut: {} | Temps de réponse: {}ms",
                    method, path, statusCode, duration);
        }));
    }

    @Override
    public int getOrder() {
        // Définit la priorité (plus le chiffre est bas, plus il est prioritaire)
        return -1;
    }
}
