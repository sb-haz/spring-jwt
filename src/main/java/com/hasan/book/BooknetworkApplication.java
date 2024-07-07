package com.hasan.book;

import com.hasan.book.role.Role;
import com.hasan.book.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication // Enable the Spring Boot application
@EnableJpaAuditing // Automatically populate the createdDate and lastModifiedDate fields
@EnableAsync // Enable asynchronous processing (eg sending an email)

public class BooknetworkApplication {

	public static void main(String[] args) {
		SpringApplication.run(BooknetworkApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository) {
		return (args) -> {
			if (roleRepository.findByName("USER").isEmpty()) {
				roleRepository.save(
						Role.builder().name("USER").build()
				);
			}

			if (roleRepository.findByName("ADMIN").isEmpty()) {
				roleRepository.save(
						Role.builder().name("ADMIN").build()
				);
			}
		};
	}

}
