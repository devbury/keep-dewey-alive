package devbury.keepdeweyalive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class KeepDeweyAliveApplication {

    public static void main(String[] args) {
        SpringApplication.run(KeepDeweyAliveApplication.class, args);
    }
}
