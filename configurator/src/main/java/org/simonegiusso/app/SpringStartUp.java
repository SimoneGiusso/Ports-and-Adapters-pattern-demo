package org.simonegiusso.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.simonegiusso.app", "org.simonegiusso.adapters"})
public class SpringStartUp {

    public static void main(String[] args) {
        SpringApplication.run(SpringStartUp.class, args);
    }

}
