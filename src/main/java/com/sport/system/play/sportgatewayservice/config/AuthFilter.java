package com.sport.system.play.sportgatewayservice.config;

import com.sport.system.play.sportgatewayservice.presenter.TokenPresenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final WebClient.Builder webClientBuilder;

    public AuthFilter(WebClient.Builder     webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {

        return new OrderedGatewayFilter((exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing  Authorization header");
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String[] parts = authHeader.split(" ");
            if (parts.length != 2 || !"Bearer".equals(parts[0])) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad Authorization structure");
            }

            return  webClientBuilder.build()
                    .get()
                    .uri("http://sport-user-service/user/validateToken?token=" + parts[1])
                    .retrieve()
                    .bodyToMono(TokenPresenter.class)
                    .map(response -> {
                        if(response == null)
                            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No Autorizado");
                        return exchange;
                    })
                    .onErrorMap(error -> { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Communication Error", error.getCause());})
                    .flatMap(chain::filter);
        },1);
    }

    public static class Config {
        // empty class as I don't need any particular configuration
    }
}