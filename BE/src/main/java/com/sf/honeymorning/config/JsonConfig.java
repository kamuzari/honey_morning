package com.sf.honeymorning.config;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

@Configuration
public class JsonConfig {

	public static class MicrosecondToLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
		@Override
		public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			long microSeconds = p.getLongValue();

			return Instant.ofEpochMilli(microSeconds / 1000)
				.atZone(ZoneOffset.UTC)
				.toLocalDateTime();
		}
	}

}
