package devbury.keepdeweyalive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@EnableAsync
@RestController
public class KeepDeweyAliveApplication {

    private static final long MIN_PING_INTERVAL = 10 * 60 * 1000;

    @Autowired
    private TaskExecutor taskExecutor;

    private RestTemplate restTemplate = new RestTemplate();

    private final Map<String, Long> lastPingByUrl = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        SpringApplication.run(KeepDeweyAliveApplication.class, args);
    }

    @RequestMapping("/ping")
    public Response ping(HttpServletRequest httpServletRequest) {
        String pingUrl = "http://" + httpServletRequest.getRemoteAddr() + "/health";

        Response response = new Response();
        response.setPingUrl(pingUrl);

        Long lastPing = lastPingByUrl.get(pingUrl);
        if (lastPing == null || now() - lastPing > MIN_PING_INTERVAL) {
            response.setMessage("pinging now");
            taskExecutor.execute(() -> asyncPing(pingUrl));
        } else {
            response.setMessage("already pinged within 10 minutes");
        }
        return response;
    }

    protected void asyncPing(String url) {
        lastPingByUrl.put(url, now());
        try {
            restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            System.out.println("Could not ping " + e.toString());
        }
    }

    protected long now() {
        return System.currentTimeMillis();
    }
}
