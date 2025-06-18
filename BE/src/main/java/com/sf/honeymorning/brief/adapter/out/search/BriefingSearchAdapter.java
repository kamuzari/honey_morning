package com.sf.honeymorning.brief.adapter.out.search;

import static com.sf.honeymorning.common.exception.model.constant.ErrorProtocol.POLICY_VIOLATION;
import static java.text.MessageFormat.format;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.BriefingRepository;
import com.sf.honeymorning.brief.adapter.out.search.mapper.BriefingSearchMapper;
import com.sf.honeymorning.brief.application.port.out.CommandBriefingSearchPort;
import com.sf.honeymorning.common.exception.model.NotFoundResourceException;

@Component
public class BriefingSearchAdapter implements CommandBriefingSearchPort {
	private final BriefingRepository briefingRepository;
	private final BriefingContentRepository briefingContentRepository;
	private final BriefingSearchMapper briefingSearchAdapterMapper;

	public BriefingSearchAdapter(
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
