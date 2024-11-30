package com.cloudbees.trainapi.ticketing;

import org.springframework.boot.SpringApplication;

public class TestTrainTicketingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(TrainTicketingServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
