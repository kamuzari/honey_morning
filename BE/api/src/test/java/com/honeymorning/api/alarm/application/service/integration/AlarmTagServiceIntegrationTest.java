package com.honeymorning.api.alarm.application.service.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.datafaker.Faker;

import com.honeymorning.api.alarm.application.port.in.AlarmTagCommandUseCase;
import com.honeymorning.api.context.integration.DefaultIntegrationTest;
import com.honeymorning.common.domain.alarm.entity.AlarmEntity;
import com.honeymorning.common.domain.alarm.entity.AlarmTagEntity;
import com.honeymorning.common.domain.alarm.entity.DefaultTags;
import com.honeymorning.common.domain.alarm.entity.TagEntity;
import com.honeymorning.common.domain.alarm.repository.AlarmRepository;
import com.honeymorning.common.domain.alarm.repository.AlarmTagRepository;
import com.honeymorning.common.domain.alarm.repository.TagRepository;

class AlarmTagServiceIntegrationTest extends DefaultIntegrationTest {

	static final long USER_ID = new Faker().number().randomNumber(10, true);

	@Autowired
	AlarmTagCommandUseCase sut;

	@Autowired
	AlarmRepository alarmRepository;

	@Autowired
	TagRepository tagRepository;

	@Autowired
	AlarmTagRepository alarmTagRepository;

	private AlarmEntity userAlarmEntity;

	@BeforeEach
	void setUp() {
		List<TagEntity> tagEntities = Arrays.stream(DefaultTags.values())
			.map(tag -> new TagEntity(tag.getWord()))
			.toList();
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
	@DisplayName("카테고리를 추가한다")
	void testAdd() {
		//given
		int addTagCount = 2;
		String addingTag1 = "정치";
		String addingTag2 = "경제";

		//when
		sut.add(USER_ID, addingTag1);
		sut.add(USER_ID, addingTag2);
		List<AlarmTagEntity> myAlarmWithTag = alarmTagRepository.findByAlarmWithTag(userAlarmEntity);

		//then
		assertThat(myAlarmWithTag).hasSize(addTagCount);
		assertThat(
			myAlarmWithTag.stream().map(alarmTag -> alarmTag.getTagEntity().getWord()).collect(Collectors.joining()))
			.contains(addingTag1, addingTag2);
	}

	@Test
	@DisplayName("카테고리를 삭제한다")
	void testRemove() {
		//given
		int expectedTagCount = 1;
		String addingTag2 = "경제";
		String addingTag1 = "정치";
		alarmTagRepository.save(new AlarmTagEntity(userAlarmEntity, tagRepository.findByWord(addingTag1).get()));
		alarmTagRepository.save(new AlarmTagEntity(userAlarmEntity, tagRepository.findByWord(addingTag2).get()));
		String removalTag = addingTag1;

		//when
		sut.remove(USER_ID, removalTag);
		List<AlarmTagEntity> myAlarmWithTag = alarmTagRepository.findByAlarmWithTag(userAlarmEntity);

		//then
		assertThat(myAlarmWithTag).hasSize(expectedTagCount);
		assertThat(myAlarmWithTag.stream()
			.map(alarmTag -> alarmTag.getTagEntity().getWord())
			.collect(Collectors.joining())).contains(addingTag2);
	}

	@Test
	@DisplayName("카테고리를 삭제할때, 없는 것을 삭제해도 예외 발생하지 않고 삭제되지도 않는다")
	void testNotExistRemove() {
		//given
		int expectedTagCount = 2;
		String addingTag2 = "경제";
		String addingTag1 = "정치";
		alarmTagRepository.save(new AlarmTagEntity(userAlarmEntity, tagRepository.findByWord(addingTag1).get()));
		alarmTagRepository.save(new AlarmTagEntity(userAlarmEntity, tagRepository.findByWord(addingTag2).get()));

		//when
		sut.remove(USER_ID, "사회");
		List<AlarmTagEntity> myAlarmWithTag = alarmTagRepository.findByAlarmWithTag(userAlarmEntity);

		//then
		assertThat(myAlarmWithTag).hasSize(expectedTagCount);
		assertThat(myAlarmWithTag.stream()
			.map(alarmTag -> alarmTag.getTagEntity().getWord())
			.collect(Collectors.joining())).contains(addingTag2);
	}

}