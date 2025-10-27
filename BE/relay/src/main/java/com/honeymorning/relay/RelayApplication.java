package com.honeymorning.relay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.honeymorning.common.config.ApplicationEventConfig;
import com.honeymorning.common.config.JasyptConfig;

@Import(value = {ApplicationEventConfig.class, JasyptConfig.class})
@SpringBootApplication
public class RelayApplication {

	public static void main(String[] args) {
		SpringApplication.run(RelayApplication.class, args);
	}

}
