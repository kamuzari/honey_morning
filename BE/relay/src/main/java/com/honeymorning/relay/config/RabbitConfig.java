package com.honeymorning.relay.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RabbitConfig {
	@Bean
	public Jackson2JsonMessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
		Jackson2JsonMessageConverter jsonMessageConverter) {

		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jsonMessageConverter);
		rabbitTemplate.setMandatory(true);
		rabbitTemplate.setConfirmCallback(configureAcknowledge(rabbitTemplate));
		rabbitTemplate.setReturnsCallback(configureComeBack(rabbitTemplate));
		rabbitTemplate.setRetryTemplate(configureRetryTemplate());
		return rabbitTemplate;
	}

	@Bean
	public MessageRecoverer recoverer() {
		return new RejectAndDontRequeueRecoverer();
	}

	private RetryTemplate configureRetryTemplate() {
		RetryTemplate retryTemplate = new RetryTemplate();

		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
		retryPolicy.setMaxAttempts(5);
		retryTemplate.setRetryPolicy(retryPolicy);

		ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setInitialInterval(1000L);
		backOffPolicy.setMultiplier(2.0);
		backOffPolicy.setMaxInterval(5000L);
		retryTemplate.setBackOffPolicy(backOffPolicy);

		return retryTemplate;
	}

	private RabbitTemplate.ReturnsCallback configureComeBack(RabbitTemplate rabbitTemplate) {
		return returned -> log.error("Message returned from Exchange. Reason: {}, Returned message: {}, request : {}",
			returned.getReplyText(),
			returned.getMessage(),
			rabbitTemplate);
	}

	private RabbitTemplate.ConfirmCallback configureAcknowledge(RabbitTemplate rabbitTemplate) {
		return (CorrelationData correlationData, boolean ack, String cause) -> {
			if (!ack) {
				log.error("delivery to Exchange failed: {}, data: {}, request: {}",
					cause,
					correlationData,
					rabbitTemplate);
			}
		};
	}
}



