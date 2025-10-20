package com.krillinator.demo_5;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	/* @ServiceConnection annotation - Spring 3.1+ Feature that:
	* 	Wires up your container,
	*	Overrides your spring.r2dbc (or JDBC) properties automatically,
	*	Runs migrations against it (e.g., Flyway).
	* */
	PostgreSQLContainer<?> postgresContainer() {
		return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
				.withStartupTimeout(Duration.ofSeconds(60))
				.waitingFor(Wait.forListeningPort());
	}

}
