package com.sf.honeymorning.alarm.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sf.honeymorning.alarm.controller.dto.response.AlarmTagResponseDto;
import com.sf.honeymorning.alarm.domain.entity.Alarm;
import com.sf.honeymorning.alarm.domain.entity.AlarmTag;
import com.sf.honeymorning.alarm.domain.entity.DefaultTags;
import com.sf.honeymorning.alarm.domain.entity.Tag;
import com.sf.honeymorning.alarm.domain.repository.AlarmRepository;
import com.sf.honeymorning.alarm.domain.repository.AlarmTagRepository;
import com.sf.honeymorning.alarm.domain.repository.TagRepository;
import com.sf.honeymorning.context.integration.DefaultIntegrationTest;
import com.sf.honeymorning.context.infra.database.MySqlContext;

class AlarmTagServiceIntegrationTest extends DefaultIntegrationTest implements MySqlContext {

	static final long USER_ID = 1L;

	@Autowired
	AlarmTagService sut;

	@Autowired
	AlarmRepository alarmRepository;

	@Autowired
	TagRepository tagRepository;

	@Autowired
	AlarmTagRepository alarmTagRepository;

	private Alarm userAlarm;

	@BeforeEach
	void setUp() {
		List<Tag> tags =Arrays.stream(DefaultTags.values()).map(tag-> new Tag(tag.getWord())).toList();
		tagRepository.saveAll(tags);
		userAlarm = alarmRepository.save(Alarm.initialize(USER_ID));
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
		int expectedAlaraTagSize = 2;
		String addingTag1 = "정치";
		String addingTag2 = "경제";
		alarmTagRepository.save(new AlarmTag(userAlarm, tagRepository.findByWord(addingTag1).get()));
		alarmTagRepository.save(new AlarmTag(userAlarm, tagRepository.findByWord(addingTag2).get()));

		//when
		List<AlarmTagResponseDto> myAlarmTags = sut.getMyAlarmTags(USER_ID);
		//then
		assertThat(myAlarmTags).hasSize(expectedAlaraTagSize);
		assertThat(myAlarmTags.stream().map(AlarmTagResponseDto::word).collect(Collectors.joining()))
			.contains(addingTag1, addingTag2);
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
		List<AlarmTag> myAlarmWithTag = alarmTagRepository.findByAlarmWithTag(userAlarm);

		//then
		assertThat(myAlarmWithTag).hasSize(addTagCount);
		assertThat(
			myAlarmWithTag.stream().map(alarmTag -> alarmTag.getTag().getWord()).collect(Collectors.joining()))
			.contains(addingTag1, addingTag2);
	}

	@Test
	@DisplayName("카테고리를 중복으로 추가해도 예외가 발생하지 않고 추가되지도 않는다")
	void testDuplicateAdd() {
		//given
		int addTagCount = 2;
		String addingTag1 = "정치";
		String addingTag2 = "경제";

		//when
		sut.add(USER_ID, addingTag1);
		sut.add(USER_ID, addingTag2);
		sut.add(USER_ID, addingTag1);
		List<AlarmTag> myAlarmWithTag = alarmTagRepository.findByAlarmWithTag(userAlarm);

		//then
		assertThat(myAlarmWithTag).hasSize(addTagCount);
		assertThat(
			myAlarmWithTag.stream().map(alarmTag -> alarmTag.getTag().getWord()).collect(Collectors.joining()))
			.contains(addingTag1, addingTag2);
	}

	@Test
	@DisplayName("카테고리를 삭제한다")
	void testRemove() {
		//given
		int expectedTagCount = 1;
		String addingTag2 = "경제";
		String addingTag1 = "정치";
		alarmTagRepository.save(new AlarmTag(userAlarm, tagRepository.findByWord(addingTag1).get()));
		alarmTagRepository.save(new AlarmTag(userAlarm, tagRepository.findByWord(addingTag2).get()));
		String removalTag = addingTag1;

		//when
		sut.remove(USER_ID, removalTag);
		List<AlarmTag> myAlarmWithTag = alarmTagRepository.findByAlarmWithTag(userAlarm);

		//then
		assertThat(myAlarmWithTag).hasSize(expectedTagCount);
		assertThat(myAlarmWithTag.stream()
			.map(alarmTag -> alarmTag.getTag().getWord())
			.collect(Collectors.joining())).contains(addingTag2);
	}


	@Test
	@DisplayName("카테고리를 삭제할때, 없는 것을 삭제해도 예외 발생하지 않고 삭제되지도 않는다")
	void testNotExistRemove() {
		//given
		int expectedTagCount = 2;
		String addingTag2 = "경제";
		String addingTag1 = "정치";
		alarmTagRepository.save(new AlarmTag(userAlarm, tagRepository.findByWord(addingTag1).get()));
		alarmTagRepository.save(new AlarmTag(userAlarm, tagRepository.findByWord(addingTag2).get()));

		//when
		sut.remove(USER_ID, "사회");
		List<AlarmTag> myAlarmWithTag = alarmTagRepository.findByAlarmWithTag(userAlarm);

		//then
		assertThat(myAlarmWithTag).hasSize(expectedTagCount);
		assertThat(myAlarmWithTag.stream()
			.map(alarmTag -> alarmTag.getTag().getWord())
			.collect(Collectors.joining())).contains(addingTag2);
	}

}