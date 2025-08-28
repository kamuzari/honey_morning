package com.honeymorning.api.alarm.adapter.in.web.port.out;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.sf.honeymorning.alarm.adapter.in.web.dto.response.AlarmTagResponseDto;
import com.sf.honeymorning.alarm.adapter.out.persistence.AlarmTagPersistenceAdapter;
import com.sf.honeymorning.alarm.adapter.out.persistence.entity.AlarmEntity;
import com.sf.honeymorning.alarm.adapter.out.persistence.entity.AlarmTagEntity;
import com.sf.honeymorning.alarm.adapter.out.persistence.entity.DefaultTags;
import com.sf.honeymorning.alarm.adapter.out.persistence.entity.TagEntity;
import com.sf.honeymorning.alarm.adapter.out.persistence.mapper.AlarmTagPersistenceMapper;
import com.sf.honeymorning.alarm.adapter.out.persistence.repository.AlarmRepository;
import com.sf.honeymorning.alarm.adapter.out.persistence.repository.AlarmTagRepository;
import com.sf.honeymorning.alarm.adapter.out.persistence.repository.TagRepository;
import com.honeymorning.api.context.mock.MockPersistenceTest;

@Import({AlarmTagPersistenceAdapter.class, AlarmTagPersistenceMapper.class})
class AlarmTagQueryPortTest extends MockPersistenceTest {
	static final long USER_ID = 1L;

	AlarmTagQueryPort sut;

	@Autowired
	AlarmTagPersistenceAdapter sutImpl;

	@Autowired
	AlarmRepository alarmRepository;

	@Autowired
	TagRepository tagRepository;

	@Autowired
	AlarmTagRepository alarmTagRepository;

	private AlarmEntity userAlarmEntity;

	@BeforeEach
	void setUp() {
		sut=sutImpl;

		List<TagEntity> tagEntities = Arrays.stream(DefaultTags.values()).map(tag-> new TagEntity(tag.getWord())).toList();
		tagRepository.saveAll(tagEntities);
		userAlarmEntity = alarmRepository.save(AlarmEntity.initialize(USER_ID));
	}

	@AfterEach
	void tearDown() {
		alarmTagRepository.deleteAllInBatch();
		alarmRepository.deleteAllInBatch();
		tagRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("나의 카테고리를 조회한다")
	void testGetMyAlarmTags() {
		//given
		int expectedAlarmTagSize = 2;
		String addingTag1 = "정치";
		String addingTag2 = "경제";
		alarmTagRepository.save(new AlarmTagEntity(userAlarmEntity, tagRepository.findByWord(addingTag1).get()));
		alarmTagRepository.save(new AlarmTagEntity(userAlarmEntity, tagRepository.findByWord(addingTag2).get()));

		//when
		List<AlarmTagResponseDto> myAlarmTags = sut.getMyAlarmTags(USER_ID);

		//then
		assertThat(myAlarmTags).hasSize(expectedAlarmTagSize);
		assertThat(myAlarmTags.stream().map(AlarmTagResponseDto::word).collect(Collectors.joining()))
			.contains(addingTag1, addingTag2);
	}
}