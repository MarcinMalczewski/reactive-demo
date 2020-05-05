package com.example.reactivedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class ReactiveDemoApplication {

	public static void main(String[] args) {
		BlockHound.install();
		SpringApplication.run(ReactiveDemoApplication.class, args);
	}

}
