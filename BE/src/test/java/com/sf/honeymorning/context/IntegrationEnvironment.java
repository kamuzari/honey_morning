package com.sf.honeymorning.context;

import com.github.javafaker.Faker;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationEnvironment{
	public static final Faker FAKE_DATA_FACTORY = new Faker();

}
