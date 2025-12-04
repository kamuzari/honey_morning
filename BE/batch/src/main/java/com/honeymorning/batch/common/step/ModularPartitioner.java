package com.honeymorning.batch.common.step;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

public class ModularPartitioner implements Partitioner {

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		return IntStream.range(0, gridSize)
			.boxed()
			.collect(Collectors.toMap(
				partitionId -> "partition - " + partitionId,
				partitionId -> {
					ExecutionContext context = new ExecutionContext();
					context.putInt("partition", partitionId);
					context.putInt("modular", gridSize);
					return context;
				}
			));
	}
}
