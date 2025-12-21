package com.honeymorning.relay.briefing.adapter.out.search;

import static com.honeymorning.common.exception.constant.ErrorProtocol.POLICY_VIOLATION;
import static java.text.MessageFormat.format;

import org.springframework.stereotype.Component;

import com.honeymorning.common.domain.briefing.entity.BriefingEntity;
import com.honeymorning.common.domain.briefing.repository.BriefingRepository;
import com.honeymorning.common.exception.NotFoundResourceException;
import com.honeymorning.relay.briefing.adapter.out.search.mapper.BriefingSearchMapper;
import com.honeymorning.relay.briefing.adapter.out.search.repository.BriefingContentRepository;
import com.honeymorning.relay.briefing.application.port.out.CommandBriefingDocumentPort;

@Component
public class BriefingDocumentAdapter implements CommandBriefingDocumentPort {
	private final BriefingRepository briefingRepository;
	private final BriefingContentRepository briefingContentRepository;
	private final BriefingSearchMapper briefingSearchAdapterMapper;

	public BriefingDocumentAdapter(
		BriefingRepository briefingRepository,
		BriefingSearchMapper briefingSearchAdapterMapper,
		BriefingContentRepository briefingContentRepository) {

		this.briefingRepository = briefingRepository;
		this.briefingSearchAdapterMapper = briefingSearchAdapterMapper;
		this.briefingContentRepository = briefingContentRepository;
	}

	public void reflect(Long briefingId) {
		BriefingEntity briefingEntity = briefingRepository.findByIdWithQuizzesAndTopicModel(briefingId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("검색 데이터 반영이 안된 브리핑 콘텐츠 입니다. briefingId -> {0}", briefingId)
				, POLICY_VIOLATION));
		var briefingContentDocument = briefingSearchAdapterMapper.toDocument(briefingEntity);
		briefingContentRepository.save(briefingContentDocument);
	}
}
