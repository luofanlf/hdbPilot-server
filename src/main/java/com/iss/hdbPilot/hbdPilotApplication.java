package com.iss.hdbPilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.iss.hdbPilot.mapper")
public class hbdPilotApplication {

	public static void main(String[] args) {
		SpringApplication.run(hbdPilotApplication.class, args);
	}

}
