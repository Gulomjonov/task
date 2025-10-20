package uz.sap.task.controller;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApplyController {
    private final RuntimeService runtimeService;

    public ApplyController(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @PostMapping("/apply")
    public ResponseEntity<?> apply(@RequestBody Map<String, Object> body) {
        // пример: body содержит clientId, phone и т.д.
        var vars = body;
        var pi = runtimeService.startProcessInstanceByKey("checkClientProcess", vars);
        return ResponseEntity.ok(Map.of("processInstanceId", pi.getId()));
    }
}

