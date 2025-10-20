package uz.sap.task.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uz.sap.task.model.AuditLog;
import uz.sap.task.repository.AuditLogRepository;

import java.util.Map;

@Component("checkExternalDelegate")
public class CheckExternalDelegate implements JavaDelegate {

    private final WebClient webClient;
    private final AuditLogRepository auditRepo;

    public CheckExternalDelegate(AuditLogRepository auditRepo) {
        this.webClient = WebClient.create("http://app:9000"); // в Docker-сети используем имя сервиса app
        this.auditRepo = auditRepo;
    }

    @Override
    public void execute(DelegateExecution execution) {
        var clientId = execution.getVariable("clientId");
        var resp = webClient.post()
                .uri("/external/check")
                .bodyValue(Map.of("clientId", clientId))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        assert resp != null;
        String status = (String) resp.get("status");

        // логируем
        auditRepo.save(new AuditLog(execution.getProcessInstanceId(), "CHECK_EXTERNAL", resp.toString()));

        if ("SUCCESS".equals(status)) {
            execution.setVariable("externalStatus", "SUCCESS");
        } else {
            execution.setVariable("externalStatus", "FAILED");
            // бросим BPMN error, либо просто пометим и процесс пойдет по ветке error
            throw new RuntimeException("External check failed");
        }
    }
}
