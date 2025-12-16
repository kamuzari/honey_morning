package com.honeymorning.batch.alarm.adapter.out.persistence.query;

import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.honeymorning.common.domain.event.entity.OutBoxAlarmEventEntity;

public class AlarmOutBoxEventWriteQueryGenerator {
	public String getSql() {
		return String.join(
			" ",
			AlarmWriteQuery.INSERT_INTO.query,
			AlarmWriteQuery.VALUES.query
		);
	}

	public ItemSqlParameterSourceProvider<OutBoxAlarmEventEntity> getOutBoxAlarmEventItemSqlParameterSourceProvider() {
		return item -> {
			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue("alarmId", item.getAlarmId());
			params.addValue("eventStatus", item.getEventStatus().name());
			params.addValue("eventType", item.getEventType());
			params.addValue("payload", item.getPayload());
			params.addValue("createdAt", item.getCreatedAt());
			return params;
		};
	}

	enum AlarmWriteQuery {
		INSERT_INTO("""
			INSERT INTO outbox_alarm_event (alarm_id, event_status, event_type, payload, created_at)
			"""),
		VALUES("""
			VALUES (:alarmId, :eventStatus, :eventType, :payload, :createdAt)
			""");

		final String query;

		AlarmWriteQuery(String query) {
			this.query = query;
		}
	}
}
