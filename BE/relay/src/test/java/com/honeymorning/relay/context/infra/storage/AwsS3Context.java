package com.honeymorning.relay.context.infra.storage;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * @Testcontainers, @Container 이 애노테이션이 복병
 * 1. 공식 문서에서의 기재 사항
 * Testcontainers 공식 문서(Manual lifecycle control)를 보면 다음과 같은 취지의 내용이 있습니다.
 * @Testcontainers의 한계: "JUnit Jupiter 확장(Extension) 방식은 컨테이너의 생명주기를 테스트 클래스나 메서드에 결속시킨다."라고 명시되어 있습니다.
 * 권장 사항: 여러 테스트 클래스에서 컨테이너를 공유하여 성능을 높이려면 "Singleton Container Pattern"을 사용하라고 별도의 섹션으로 가이드하고 있습니다. 즉, 어노테이션 기반은 "공유"를 위해 설계된 것이 아님을 시사합니다.
 * 2. 왜 복병(병목)인가?
 * 컨테이너 기동 오버헤드: 도커 컨테이너 하나가 뜨는 데 보통 5~20초가 걸립니다. 테스트 클래스가 20개인데 @Container를 사용하면, 매 클래스마다 이 시간을 기다려야 하므로 전체 테스트 시간이 수 분 이상 늘어납니다.
 * 포트 오염 및 캐시 충돌: 앞서 겪으신 것처럼, 스프링은 컨텍스트를 재사용하려 하는데(Context Caching), @Container는 컨테이너를 죽여버립니다. 이 불일치(Mismatch)가 발생하면 테스트가 깨지기 시작하고 이를 디버깅하는 데 많은 시간이 낭비됩니다.
 * 3. "Ryuk" 컨테이너의 존재 (2026년 기준 표준)
 * 질문자님이 @Container를 떼고 static 필드로만 선언해도 괜찮은 이유가 공식 문서에 있습니다.
 * Testcontainers는 실행 시 Ryuk이라는 사이드카 컨테이너를 자동으로 띄웁니다.
 * 이 녀석은 테스트 프로세스(JVM)가 종료되는 것을 감지하면, @Container 어노테이션이 없더라도 사용했던 모든 컨테이너를 자동으로 정리합니다.
 * 따라서 "끄는 것은 Ryuk에게 맡기고, 우리는 켜는 것만 신경 쓴다"는 전략이 가장 효율적입니다.
 * 요약: 2026년 실무 가이드라인
 * 단일 클래스 테스트: @Container 사용 OK.
 * 전체 통합 테스트 세트:
 * @Testcontainers, @Container 사용 지양.
 * static 필드 + if (!container.isRunning()) container.start(); 방식(Singleton) 혹은 Spring Boot 3.x+의 @ServiceConnection 기반 공유 설정 사용 권장.
 * 결국 "어노테이션은 편리함을 주지만, 제어권(Lifecycle)을 프레임워크에 뺏기게 만든다"는 점이 병목의 핵심입니다. 수동 제어(Manual Start)로 바꾸신 것은 성능과 안정성 측면에서 매우 탁월한 선택입니다. Spring Boot의 Testcontainers 통합 가이드에서도 이와 같은 공유 최적화를 강조하고 있습니다.
 */
public interface AwsS3Context {
	String IMAGE_NAME = "localstack/localstack";
	String AWS_S3_CLIENT_ENDPOINT = "aws.s3.client.endpoint";

	LocalStackContainer localStack = createLocalStack();

	private static LocalStackContainer createLocalStack() {
		LocalStackContainer container = new LocalStackContainer(DockerImageName.parse(IMAGE_NAME))
			.withServices(LocalStackContainer.Service.S3)
			.withReuse(true);
		container.start();

		return container;
	}

	@DynamicPropertySource
	static void setRabbitMqProperties(DynamicPropertyRegistry registry) {
		registry.add(AWS_S3_CLIENT_ENDPOINT, localStack::getEndpoint);
	}
}