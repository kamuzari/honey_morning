package com.honeymorning.relay.briefing.adapter.out.persistence;

import static java.text.MessageFormat.format;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.honeymorning.common.exception.NotFoundResourceException;
import com.honeymorning.common.exception.constant.ErrorProtocol;
import com.honeymorning.relay.briefing.application.domain.RetryingFailTts;
import com.honeymorning.relay.briefing.application.port.out.CommandFailTtsEventPort;
import com.honeymorning.relay.briefing.application.port.out.TtsCompensationPort;
import com.honeymorning.relay.event.entity.FailTtsEventEntity;
import com.honeymorning.relay.event.repository.FailTtsEventEntityRepository;

@Component
public class FailTtsEventPersistenceAdapter implements CommandFailTtsEventPort, TtsCompensationPort {
	private final FailTtsEventEntityRepository failTtsEventEntityRepository;

	public FailTtsEventPersistenceAdapter(FailTtsEventEntityRepository failTtsEventEntityRepository) {
		this.failTtsEventEntityRepository = failTtsEventEntityRepository;
	}

	private static RetryingFailTts toEndingFailTts(FailTtsEventEntity failTtsEventEntity) {
		return new RetryingFailTts(
			failTtsEventEntity.getId(),
			failTtsEventEntity.getBriefingId(),
			failTtsEventEntity.getEventStatus()
		);
	}

	public void save(Long briefingId) {
		failTtsEventEntityRepository.save(
			new FailTtsEventEntity(briefingId)
		);
	}

	@Transactional(transactionManager = "eventTransactionManager")
	public void reflect(RetryingFailTts failTts) {
		FailTtsEventEntity failTtsEvent = getFailTtsEventId(failTts.getFailTtsEventId());
		failTtsEvent.complete(failTts.getEventStatus());
	}

	@Transactional(transactionManager = "eventTransactionManager")
	public RetryingFailTts loadTopOnSkipLock() {
		return failTtsEventEntityRepository.findFailStatusForUpdateSkipLocked(1L)
			.map(FailTtsEventPersistenceAdapter::toEndingFailTts)
			.orElseGet(RetryingFailTts::createEmpty);
	}

	private FailTtsEventEntity getFailTtsEventId(Long failTtsEventId) {
		return failTtsEventEntityRepository.findById(failTtsEventId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("이벤트가 존재하지 않습니다.. failTtsEventId -> {0}", failTtsEventId)
				, ErrorProtocol.POLICY_VIOLATION));
	}
}
