package com.sf.honeymorning.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RabbitConfig {
	public static final String AI_GENERATIVE_ALARM_CONTENTS_QUEUE_NAME = "ai.generative.alarm_contents";
	public static final String AI_GENERATED_ALARM_CONTENTS_RESPONSE_QUEUE_NAME = "ai.generated.alarm_contents_response";
	public static final String DEAD_LETTER_EXCHANGE_HEADER_NAME = "x-dead-letter-exchange";
	private static final String DEAD_LETTER_ROUTING_KEY_HEADER_NAME = "x-dead-letter-routing-key";

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

	@DependsOn("createGenerativeAIAlarmContentsDlq")
	@Bean
	public Queue createGenerativeAIAlarmContentsQueue() {
		return QueueBuilder
			.durable(AI_GENERATIVE_ALARM_CONTENTS_QUEUE_NAME)
			.withArgument(DEAD_LETTER_EXCHANGE_HEADER_NAME, "")
			.withArgument(
				DEAD_LETTER_ROUTING_KEY_HEADER_NAME,
				generateDeadLetterQueueName(AI_GENERATIVE_ALARM_CONTENTS_QUEUE_NAME)
			).build();
	}

	@Bean
	public Queue createGenerativeAIAlarmContentsDlq() {
		return QueueBuilder
			.durable(generateDeadLetterQueueName(AI_GENERATIVE_ALARM_CONTENTS_QUEUE_NAME))
			.build();
	}

	@DependsOn("createGeneratedAIAlarmContentsResponseDlq")
	@Bean
	public Queue createGeneratedAIAlarmContentsResponseQueue() {
		return QueueBuilder
			.durable(
				AI_GENERATED_ALARM_CONTENTS_RESPONSE_QUEUE_NAME)
			.withArgument(DEAD_LETTER_EXCHANGE_HEADER_NAME, "")
			.withArgument(
				DEAD_LETTER_ROUTING_KEY_HEADER_NAME
				, AI_GENERATED_ALARM_CONTENTS_RESPONSE_QUEUE_NAME + ".dlq")
			.build();
	}

	@Bean
	public Queue createGeneratedAIAlarmContentsResponseDlq() {
		return QueueBuilder
			.durable(generateDeadLetterQueueName(AI_GENERATED_ALARM_CONTENTS_RESPONSE_QUEUE_NAME))
			.build();
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

	private String generateDeadLetterQueueName(String queueName) {
		return String.join(".", queueName, "dlq");
	}

}



