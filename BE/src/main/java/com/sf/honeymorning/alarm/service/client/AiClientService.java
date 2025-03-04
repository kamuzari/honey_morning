package com.sf.honeymorning.alarm.service.client;

import com.rabbitmq.client.Channel;
import com.sf.honeymorning.alarm.service.AlarmContentService;
import com.sf.honeymorning.alarm.service.dto.response.AiResponseDto;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.common.exception.model.constant.ErrorProtocol;
import com.sf.honeymorning.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.MessageFormat;

@Transactional(readOnly = true)
@Component
public class AiClientService {
    private static final Logger log = LoggerFactory.getLogger(AiClientService.class);
    public static final String SUBSCRIBE_QUEUE_NAME = RabbitConfig.AI_GENERATED_ALARM_CONTENTS_RESPONSE_QUEUE_NAME;

    private final AlarmContentService alarmContentService;

    public AiClientService(AlarmContentService alarmContentService) {
        this.alarmContentService = alarmContentService;
    }

    @RabbitListener(queues = SUBSCRIBE_QUEUE_NAME, ackMode = "MANUAL")
    public void createAlarmContents(AiResponseDto response, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {

        try {
            alarmContentService.create(response);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            try {
                channel.basicNack(tag, false, false);
            } catch (IOException ex) {
                throw new AmqpException("NACK 전송 실패", ex);
            }

            throw new BusinessException(
                    MessageFormat.format("ai 로부터 받은 콘텐츠 사후처리에 실패했습니다. response : {0}", response),
                    ErrorProtocol.BUSINESS_VIOLATION
            );
        }
    }
}
