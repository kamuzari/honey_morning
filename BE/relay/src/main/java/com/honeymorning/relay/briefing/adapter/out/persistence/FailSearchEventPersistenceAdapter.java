package com.honeymorning.relay.briefing.adapter.out.persistence;

import static java.text.MessageFormat.format;

import org.springframework.stereotype.Component;

import com.honeymorning.common.exception.NotFoundResourceException;
import com.honeymorning.common.exception.constant.ErrorProtocol;
import com.honeymorning.relay.briefing.application.domain.RetryingFailSearchDocument;
import com.honeymorning.relay.briefing.application.port.out.CommandFailSearchEventPort;
import com.honeymorning.relay.briefing.application.port.out.SearchDocumentCompensationPort;
import com.honeymorning.relay.event.entity.FailSearchEventEntity;
import com.honeymorning.relay.event.repository.FailSearchEventEntityRepository;

@Component
public class FailSearchEventPersistenceAdapter implements CommandFailSearchEventPort, SearchDocumentCompensationPort {
	private final FailSearchEventEntityRepository failSearchEventEntityRepository;

	public FailSearchEventPersistenceAdapter(FailSearchEventEntityRepository failSearchEventEntityRepository) {
		this.failSearchEventEntityRepository = failSearchEventEntityRepository;
	}

	@Override
	public void save(Long briefingId) {
		failSearchEventEntityRepository.save(new FailSearchEventEntity(briefingId));
	}

	@Override
	public RetryingFailSearchDocument loadTopOnSkipLock() {
		return failSearchEventEntityRepository.findFailStatusForUpdateSkipLocked(1L)
			.map(failSearchEventEntity -> new RetryingFailSearchDocument(
				failSearchEventEntity.getId(),
				failSearchEventEntity.getBriefingId(),
				failSearchEventEntity.getEventStatus()
			))
			.orElse(RetryingFailSearchDocument.createEmpty());
	}

	@Override
	public void reflect(RetryingFailSearchDocument retryingFailSearchDocument) {
		var failSearchEventEntity = failSearchEventEntityRepository.findById(
				retryingFailSearchDocument.getFailSearchEventId())
			.orElseThrow(() -> new NotFoundResourceException(
				format("이벤트가 존재하지 않습니다.. failSearchDocumentEventId -> {0}", retryingFailSearchDocument)
				, ErrorProtocol.POLICY_VIOLATION));
		failSearchEventEntity.complete(retryingFailSearchDocument.getEventStatus());
	}
}
