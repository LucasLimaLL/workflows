package br.com.lucaslima.orchestrator.adapters.web.configuration;

import br.com.lucaslima.orchestrator.adapters.web.HttpLoggingExchangeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {
    @Bean
    public WebClient webClient(HttpLoggingExchangeFilter httpLoggingExchangeFilter) {
        return WebClient.builder().filter(httpLoggingExchangeFilter).build();
    }
}
