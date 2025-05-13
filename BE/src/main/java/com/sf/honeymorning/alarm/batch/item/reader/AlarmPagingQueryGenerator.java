package com.sf.honeymorning.alarm.batch.item.reader;

import static com.sf.honeymorning.alarm.batch.item.reader.AlarmPagingQueryGenerator.AlarmPagingQuery.FROM;
import static com.sf.honeymorning.alarm.batch.item.reader.AlarmPagingQueryGenerator.AlarmPagingQuery.GROUP;
import static com.sf.honeymorning.alarm.batch.item.reader.AlarmPagingQueryGenerator.AlarmPagingQuery.SELECT;
import static com.sf.honeymorning.alarm.batch.item.reader.AlarmPagingQueryGenerator.AlarmPagingQuery.SORT;
import static com.sf.honeymorning.alarm.batch.item.reader.AlarmPagingQueryGenerator.AlarmPagingQuery.WHERE;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Map;

import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.jdbc.core.RowMapper;

import com.sf.honeymorning.alarm.batch.item.dto.ReadyAlarmDto;

public class AlarmPagingQueryGenerator {
	public MySqlPagingQueryProvider createQuery() {
		var queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause(SELECT.query);
		queryProvider.setFromClause(FROM.query);
		queryProvider.setWhereClause(WHERE.query);
		queryProvider.setGroupClause(GROUP.query);
		queryProvider.setSortKeys(Map.of(SORT.query, AlarmPagingQuery.SORT_ORDER));

		return queryProvider;
	}

	public Map<String, Object> getParameters(
		LocalTime startTime,
		LocalTime endTime,
		Long today,
		int modular,
		int partition) {

		return Map.of(
			"startTime", startTime,
			"endTime", endTime,
			"dayOfWeekMask", today,
			"partition", partition,
			"modular", modular
		);
	}

	public RowMapper<ReadyAlarmDto> getRowMapper() {
		return AlarmPagingQuery.TO_ALARM_DTO;
	}

	enum AlarmPagingQuery {

		SELECT("SELECT user_id, GROUP_CONCAT(t.word ORDER BY t.word SEPARATOR ', ') AS tags "),
		FROM("""
			FROM alarms a
			JOIN alarm_tags at ON a.id = at.alarm_id
			JOIN tags t ON at.tag_id = t.id
			"""),
		WHERE("""
			WHERE is_active = true
			AND (day_of_the_weeks & :dayOfWeekMask) != 0
			AND wake_up_time BETWEEN :startTime AND :endTime
			AND MOD(user_id, :modular) = :partition
			"""),
		GROUP("GROUP BY user_id"),
		SORT("user_id");

		private static final Order SORT_ORDER = Order.ASCENDING;
		private static final RowMapper<ReadyAlarmDto> TO_ALARM_DTO =
			(rs, rowNum) -> new ReadyAlarmDto(
				rs.getLong("user_id"),
				Arrays.stream(
					rs.getString("tags").split(",")
				).toList()
			);

		private final String query;

		AlarmPagingQuery(String query) {
			this.query = query;
		}
	}
}
