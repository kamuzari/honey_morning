package com.sf.honeymorning.alarm.batch.item.writer;

import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.sf.honeymorning.alarm.batch.outbox.OutBoxAlarmEvent;

public class AlarmOutBoxEventWriteQueryGenerator {
	public String getSql() {
		return String.join(
			" ",
			AlarmWriteQuery.INSERT_INTO.query,
			AlarmWriteQuery.VALUES.query
		);
	}

	public ItemSqlParameterSourceProvider<OutBoxAlarmEvent> getOutBoxAlarmEventItemSqlParameterSourceProvider() {
		return item -> {
			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue("alarmId", item.getAlarmId());
			params.addValue("eventStatus", item.getEventStatus().name());
			params.addValue("eventType", item.getEventType());
			params.addValue("payload", item.getPayload());
			params.addValue("createAt", item.getCreateAt());
			return params;
		};
	}

	enum AlarmWriteQuery {
		INSERT_INTO("""
			INSERT INTO outbox_alarm_event (alarm_id, event_status, event_type, payload, create_at)
			"""),
		VALUES("""
			VALUES (:alarmId, :eventStatus, :eventType, :payload, :createAt)
			""");

		final String query;

		AlarmWriteQuery(String query) {
			this.query = query;
		}
	}
}
