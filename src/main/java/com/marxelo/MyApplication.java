package com.marxelo;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.marxelo.listeners.MyApplicationListener;

import lombok.extern.slf4j.Slf4j;


@SpringBootApplication
@EnableAutoConfiguration
@Slf4j
public class MyApplication {

    public static void main(String[] args) {
        SpringApplication sa = new SpringApplication();

        sa.addListeners(new MyApplicationListener());

        sa.setSources(new HashSet<>(
            Arrays.asList(MyApplication.class.getName())));

        ConfigurableApplicationContext context = sa.run(args);

        ThreadPoolTaskScheduler bean = context.getBean(
            ThreadPoolTaskScheduler.class);

        // Aguardar o último envio de métrica
        try {
            Thread.sleep(15L);
            System.out.println("\n");
            log.info("Ready And Alive \\o/. Let's go baby!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        bean.shutdown();
    }
}
