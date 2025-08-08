package br.com.lucaslima.orchestrator.configuration;

import br.com.lucaslima.orchestrator.application.services.routing.DefaultRouteStrategy;
import br.com.lucaslima.orchestrator.application.services.routing.ExactStatusStrategy;
import br.com.lucaslima.orchestrator.application.services.routing.FamilyStatusStrategy;
import br.com.lucaslima.orchestrator.application.services.routing.ListStatusStrategy;
import br.com.lucaslima.orchestrator.application.services.routing.RangeStatusStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NextStepResolverConfiguration {
    @Bean public ExactStatusStrategy exactStatusStrategy() { return new ExactStatusStrategy(); }
    @Bean public ListStatusStrategy listStatusStrategy() { return new ListStatusStrategy(); }
    @Bean public RangeStatusStrategy rangeStatusStrategy() { return new RangeStatusStrategy(); }
    @Bean public FamilyStatusStrategy familyStatusStrategy() { return new FamilyStatusStrategy(); }
    @Bean public DefaultRouteStrategy defaultRouteStrategy() { return new DefaultRouteStrategy(); }
}
