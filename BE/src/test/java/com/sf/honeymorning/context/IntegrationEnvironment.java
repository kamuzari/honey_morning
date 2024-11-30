package com.sf.honeymorning.context;

import org.springframework.boot.test.context.SpringBootTest;

import com.github.javafaker.Faker;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationEnvironment {
	protected static final Faker FAKE_DATA_FACTORY = new Faker();
}
