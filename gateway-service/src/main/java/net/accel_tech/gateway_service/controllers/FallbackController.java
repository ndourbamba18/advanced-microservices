package net.accel_tech.gateway_service.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/product-service")
    public Mono<String> productServiceFallback() {
        return Mono.just("Le service produit est temporairement indisponible. Veuillez réessayer plus tard.");
    }

    @GetMapping("/category-service")
    public Mono<String> categoryServiceFallback() {
        return Mono.just("Le service catégorie ne répond pas. Maintenance en cours.");
    }
}
