package net.accel_tech.gateway_service.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/category-service")
    public Mono<ResponseEntity<Map<String, String>>> categoryServiceFallback() {
        Map<String, String> response = new HashMap<>();
        //response.put("message", "Service indisponible");
        response.put("message", "Le service catégorie ne répond pas. Maintenance en cours.");
        response.put("status", "error");
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }

    @RequestMapping("/product-service")
    public Mono<ResponseEntity<Map<String, String>>> productServiceFallback() {
        Map<String, String> response = new HashMap<>();
        //response.put("message", "Service indisponible");
        response.put("message", "Le service produit est temporairement indisponible. Veuillez réessayer plus tard.");
        response.put("status", "error");
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
}
