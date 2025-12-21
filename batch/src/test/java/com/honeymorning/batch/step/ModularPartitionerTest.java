package com.honeymorning.batch.step;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.ExecutionContext;

import com.honeymorning.batch.common.step.ModularPartitioner;

@ExtendWith(MockitoExtension.class)
class ModularPartitionerTest {
	@InjectMocks
	ModularPartitioner modularPartitioner;

	@Test
	@DisplayName("쓰레드 풀 사이즈에 맞게 모듈러 연산을 통해 파티션을 나눈다")
	void test() {
		//given
		int threadPoolSize = 3;

		//when
		Map<String, ExecutionContext> partitionContext = modularPartitioner.partition(threadPoolSize);

		//then
		AtomicInteger partitionId = new AtomicInteger(0);
		Assertions.assertThat(partitionContext).hasSize(threadPoolSize);
		partitionContext.keySet().forEach(
			key -> {
				ExecutionContext context = partitionContext.get(key);
				int partition = context.getInt("partition");
				int modular = context.getInt("modular");
				Assertions.assertThat(partition).isEqualTo(partitionId.getAndIncrement());
				Assertions.assertThat(modular).isEqualTo(threadPoolSize);
			}
		);
	}

}