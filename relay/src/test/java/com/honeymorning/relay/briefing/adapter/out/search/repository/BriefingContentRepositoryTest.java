package com.honeymorning.relay.briefing.adapter.out.search.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.honeymorning.common.domain.briefing.constraint.QuizConstraint;
import com.honeymorning.relay.briefing.adapter.out.search.index.BriefingContentDocument;
import com.honeymorning.relay.context.mock.BriefingMockGenerator;

@DataElasticsearchTest
@Testcontainers
class BriefingContentRepositoryTest {

	@ServiceConnection
	@Container
	static ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer(
		"docker.elastic.co/elasticsearch/elasticsearch:8.13.4")
		.withEnv("discovery.type", "single-node")
		.withEnv("xpack.security.enabled", "false")
		.withEnv("xpack.security.http.ssl.enabled", "false")
		.withEnv("ES_JAVA_OPTS", "-Xms1g -Xmx1g")
		.withEnv("TZ", "Asia/Seoul")
		.withExposedPorts(9200, 9300)
		.withCommand("bash", "-c",
			"elasticsearch-plugin install --batch analysis-nori && /usr/local/bin/docker-entrypoint.sh");

	@Autowired
	BriefingContentRepository briefingContentRepository;

	@AfterEach
	void tearDown() {
		briefingContentRepository.deleteAll();
	}

	@DisplayName("자신이 가지고 있는 데이터에서 특정 키워드로 검색한다")
	@Test
	void testSearchByUserIdAndAll() {
		// given
		createDemoDatas(10);

		// when
		Page<BriefingContentDocument> result = briefingContentRepository.searchByUserIdAndAll(1L, "테스트 데이터",
			PageRequest.of(0, 10));

		// then
		assertThat(result.getContent().size()).isEqualTo(1);
		assertThat(result.getTotalElements()).isEqualTo(1);
	}

	@DisplayName("자신이 가지고 있는 데이터에서 페이징하여 최대 10개만 가져온다")
	@Test
	void testSearchByUserIdAndAllGettingMax10Size() {
		// given
		int pageMaxSize = 10;
		int size = 12;
		createDemoDatas(size, 1L);

		// when
		Page<BriefingContentDocument> result = briefingContentRepository.searchByUserIdAndAll(1L, "테스트 데이터",
			PageRequest.of(0, pageMaxSize));

		// then
		assertThat(result.getContent().size()).isEqualTo(pageMaxSize);
		assertThat(result.getTotalElements()).isEqualTo(size);
	}

	void createDemoDatas(int size, Long fixedUserId) {
		briefingContentRepository.saveAll(
			LongStream.rangeClosed(1, size)
				.mapToObj(userId ->
					new BriefingContentDocument(
						BriefingMockGenerator.GENERATOR.number().randomNumber(),
						fixedUserId,
						"요약 테스트 데이터" + userId,
						"장문 테스트 데이터" + userId,
						BriefingMockGenerator.GENERATOR.lorem().words(),
						createQuizIndex()
					)
				).toList());
	}

	void createDemoDatas(int size) {
		briefingContentRepository.saveAll(
			LongStream.rangeClosed(1, size)
				.mapToObj(userId ->
					new BriefingContentDocument(
						BriefingMockGenerator.GENERATOR.number().randomNumber(),
						userId,
						"요약 테스트 데이터" + userId,
						"장문 테스트 데이터" + userId,
						BriefingMockGenerator.GENERATOR.lorem().words(),
						createQuizIndex()
					)
				).toList());
	}

	List<BriefingContentDocument.QuizDocument> createQuizIndex() {
		return Stream.generate(() -> BriefingMockGenerator.GENERATOR.lorem().word())
			.map(word -> {
					return new BriefingContentDocument.QuizDocument(
						BriefingMockGenerator.GENERATOR.number().randomNumber(),
						BriefingMockGenerator.GENERATOR.lorem().sentence(1),
						List.of(BriefingMockGenerator.GENERATOR.lorem().word(),
							BriefingMockGenerator.GENERATOR.lorem().word(),
							BriefingMockGenerator.GENERATOR.lorem().word(),
							BriefingMockGenerator.GENERATOR.lorem().word())
					);
				}
			)
			.limit(QuizConstraint.TOTAL_QUIZ_SIZE)
			.toList();
	}
}