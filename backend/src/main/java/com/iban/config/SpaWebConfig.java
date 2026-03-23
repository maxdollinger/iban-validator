package com.iban.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@ConditionalOnResource(resources = "classpath:static/index.html")
public class SpaWebConfig {

    @Bean
    public RouterFunction<ServerResponse> spaRouter() {
        var index = new ClassPathResource("static/index.html");

        // Serve static files (JS, CSS, images) — only matches existing files
        var staticResources = RouterFunctions.resources("/**", new ClassPathResource("static/"));

        // SPA fallback: serve index.html for any non-API GET that didn't match a static file
        var spaFallback = RouterFunctions.route(
                RequestPredicates.GET("/**")
                        .and(RequestPredicates.path("/api/**").negate())
                        .and(RequestPredicates.path("/actuator/**").negate()),
                request -> ServerResponse.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .bodyValue(index));

        return staticResources.and(spaFallback);
    }
}
