package com.sf.honeymorning.alarm.batch.step;

import java.util.HashMap;
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
				partitionSize -> "partition - " + partitionSize,
				partitionSize -> {
					ExecutionContext context = new ExecutionContext();
					context.putInt("partitionSize", partitionSize);
					context.putInt("modular", gridSize);
					return context;
				}
			));
	}
}
