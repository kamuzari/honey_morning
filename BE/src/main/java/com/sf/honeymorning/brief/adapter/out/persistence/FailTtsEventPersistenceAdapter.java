package com.sf.honeymorning.brief.adapter.out.persistence;

import static com.sf.honeymorning.common.exception.model.constant.ErrorProtocol.POLICY_VIOLATION;
import static java.text.MessageFormat.format;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.brief.adapter.out.persistence.entity.event.FailTtsEventEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.event.FailTtsEventEntityRepository;
import com.sf.honeymorning.brief.application.domain.RetryingFailTts;
import com.sf.honeymorning.brief.application.port.out.CommandFailTtsEventPort;
import com.sf.honeymorning.brief.application.port.out.TtsCompensationPort;
import com.sf.honeymorning.common.exception.model.NotFoundResourceException;

@Component
public class FailTtsEventPersistenceAdapter implements CommandFailTtsEventPort, TtsCompensationPort {
	private final FailTtsEventEntityRepository failTtsEventEntityRepository;

	public FailTtsEventPersistenceAdapter(FailTtsEventEntityRepository failTtsEventEntityRepository) {
		this.failTtsEventEntityRepository = failTtsEventEntityRepository;
	}

	public void save(Long briefingId) {
		failTtsEventEntityRepository.save(
			new FailTtsEventEntity(briefingId)
		);
	}

	public void reflect(RetryingFailTts failTts) {
		FailTtsEventEntity failTtsEventId = getFailTtsEventId(failTts.getFailTtsEventId());
		failTtsEventId.complete(failTts.getEventStatus());
	}

	public RetryingFailTts loadTopOnSkipLock() {
		return failTtsEventEntityRepository.findFailStatusForUpdateSkipLocked(1L)
			.map(FailTtsEventPersistenceAdapter::toEndingFailTts)
			.orElseGet(RetryingFailTts::createEmpty);
	}

	private static RetryingFailTts toEndingFailTts(FailTtsEventEntity failTtsEventEntity) {
		return new RetryingFailTts(
			failTtsEventEntity.getId(),
			failTtsEventEntity.getBriefingId(),
			failTtsEventEntity.getEventStatus()
		);
	}

	private FailTtsEventEntity getFailTtsEventId(Long failTtsEventId) {
		return failTtsEventEntityRepository.findById(failTtsEventId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("이벤트가 존재하지 않습니다.. failTtsEventId -> {0}", failTtsEventId)
				, POLICY_VIOLATION));
	}
}
