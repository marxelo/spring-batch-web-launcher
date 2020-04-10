package com.marxelo;

import java.util.Arrays;
import java.util.HashSet;

import com.marxelo.configuration.MyApplicationListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import ch.qos.logback.classic.LoggerContext;

@SpringBootApplication
@EnableAutoConfiguration
public class MyApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(MyApplication.class);

	public static void main(String[] args) {
		SpringApplication sa = new SpringApplication();
		sa.addListeners(new MyApplicationListener());
		sa.setSources(new HashSet<>(
				Arrays.asList(MyApplication.class.getName())));
		ConfigurableApplicationContext context = sa.run(args);
		ThreadPoolTaskScheduler bean = context.getBean(ThreadPoolTaskScheduler.class);
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

		// Aguardar o último envio de métrica
		try {
			Thread.sleep(15000L);
			LOGGER.info("awake");
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		loggerContext.stop();
		bean.shutdown();
	}
}
