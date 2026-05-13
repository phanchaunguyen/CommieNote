package com.CommieNote.masternote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MasternoteApplication {

	public static void main(String[] args)
	{
		SpringApplication.run(MasternoteApplication.class, args);
	}

}
