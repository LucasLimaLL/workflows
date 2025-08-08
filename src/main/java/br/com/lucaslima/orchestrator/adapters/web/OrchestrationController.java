package br.com.lucaslima.orchestrator.adapters.web;

import br.com.lucaslima.orchestrator.application.ports.in.OrchestrationUseCase;
import br.com.lucaslima.orchestrator.common.JsonPayloadParser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/orchestrate")
public class OrchestrationController {

    private final OrchestrationUseCase orchestrationUseCase;

    public OrchestrationController(OrchestrationUseCase orchestrationUseCase) {
        this.orchestrationUseCase = orchestrationUseCase;
    }

    @PostMapping("/run")
    public ResponseEntity<Void> run(@RequestParam String flowId,
                                    @RequestParam(defaultValue = "false") boolean reprocess,
                                    @RequestParam(required = false, name = "lastStep") String lastExecutedStepName,
                                    @RequestBody String payloadJson) {
        Map<String, Object> initialContextMap = JsonPayloadParser.parse(payloadJson);
        orchestrationUseCase.run(flowId, initialContextMap, reprocess, lastExecutedStepName);
        return ResponseEntity.accepted().build();
    }
}
