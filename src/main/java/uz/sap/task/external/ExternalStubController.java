package uz.sap.task.external;

import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/external")
public class ExternalStubController {

    @PostMapping("/check")
    public Map<String, String> check(@RequestBody Map<String,Object> req) {
        // простая логика: можно проверить clientId или рандомно вернуть FAILED
        var rnd = new Random();
        boolean success = rnd.nextInt(100) < 80; // 80% success
        return Map.of("status", success ? "SUCCESS" : "FAILED");
    }
}
