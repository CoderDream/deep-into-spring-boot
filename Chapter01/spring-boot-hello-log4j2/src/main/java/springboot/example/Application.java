package springboot.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @RequestMapping("/")
    String home() {
        // 级别由低到高 trace<debug<info<warn<error
        logger.trace("这是一个trace日志...");
        logger.debug("这是一个debug日志...");
        // SpringBoot默认是info级别，只会输出info及以上级别的日志
        logger.info("这是一个info日志...");
        logger.warn("这是一个warn日志...");
        logger.error("这是一个error日志...");
        logger.error("Application home method call");
        return "hello";
    }

    public static void main(String[] args) {
        logger.error("Application import main call");
        SpringApplication.run(Application.class, args);
    }
}
