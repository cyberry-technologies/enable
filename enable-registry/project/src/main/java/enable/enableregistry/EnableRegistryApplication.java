package enable.enableregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EnableRegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnableRegistryApplication.class, args);
    }

}
