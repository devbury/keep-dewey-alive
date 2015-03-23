package devbury.keepdeweyalive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@EnableAsync
@RestController
public class KeepDeweyAliveApplication {

    private static final long MIN_HEALTH_CHECK_INTERVAL = 10 * 60 * 1000;

    @Autowired
    private TaskExecutor taskExecutor;

    private RestTemplate restTemplate = new RestTemplate();

    private final Map<String, Long> lastHealthCheckByUrl = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        SpringApplication.run(KeepDeweyAliveApplication.class, args);
    }

    @RequestMapping(value = "/health-check", method = RequestMethod.POST)
    public HealthCheck healthCheck(@RequestBody String baseUrl) {
        String healthCheckUrl = baseUrl + "/management/health";

        HealthCheck healthCheck = new HealthCheck();
        healthCheck.setHealthCheckUrl(healthCheckUrl);

        Long lastHealthCheck = lastHealthCheckByUrl.get(healthCheckUrl);
        if (lastHealthCheck == null || now() - lastHealthCheck > MIN_HEALTH_CHECK_INTERVAL) {
            healthCheck.setMessage("checking health now");
            taskExecutor.execute(() -> asyncHealthCheck(healthCheckUrl));
        } else {
            healthCheck.setMessage("health already checked within the last 10 minutes");
        }
        return healthCheck;
    }

    protected void asyncHealthCheck(String url) {
        lastHealthCheckByUrl.put(url, now());
        try {
            restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            // could not perform check
        }
    }

    protected long now() {
        return System.currentTimeMillis();
    }
}
