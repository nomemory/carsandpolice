package net.andreinc.carsandpolice;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.boot.SpringApplication.run;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class CarsAndPoliceApplication {

	public static void main(String[] args) {
		run(CarsAndPoliceApplication.class, args);
	}
}
