package com.dronfies.trafficinformationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ServletComponentScan
@SpringBootApplication
public class TrafficInformationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrafficInformationServiceApplication.class, args);
	}

}
