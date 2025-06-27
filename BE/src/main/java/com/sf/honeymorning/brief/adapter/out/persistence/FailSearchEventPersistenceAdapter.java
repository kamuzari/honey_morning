package com.sf.honeymorning.brief.adapter.out.persistence;

import static com.sf.honeymorning.common.exception.model.constant.ErrorProtocol.POLICY_VIOLATION;
import static java.text.MessageFormat.format;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.brief.adapter.out.persistence.entity.event.FailSearchEventEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.event.FailSearchEventEntityRepository;
import com.sf.honeymorning.brief.application.domain.RetryingFailSearchDocument;
import com.sf.honeymorning.brief.application.port.out.CommandFailSearchEventPort;
import com.sf.honeymorning.brief.application.port.out.SearchDocumentCompensationPort;
import com.sf.honeymorning.common.exception.model.NotFoundResourceException;

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
				, POLICY_VIOLATION));
		failSearchEventEntity.complete(retryingFailSearchDocument.getEventStatus());
	}
}
