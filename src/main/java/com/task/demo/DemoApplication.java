package com.task.demo;

import com.task.demo.entity.Account;
import com.task.demo.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner initData(AccountRepository repository) {
		return args -> {
			repository.save(new Account("12345", BigDecimal.valueOf(1000)));
			repository.save(new Account("67890", BigDecimal.valueOf(2000)));
		};
	}
}
