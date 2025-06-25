package fhtw.wien.energy_community_percentage_service;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableRabbit
@EnableScheduling
public class EnergyCommunityPercentageServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnergyCommunityPercentageServiceApplication.class, args);
	}

}
