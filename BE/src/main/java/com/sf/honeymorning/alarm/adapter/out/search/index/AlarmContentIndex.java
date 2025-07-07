package com.sf.honeymorning.alarm.adapter.out.search.index;

import java.time.LocalDateTime;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Document(indexName = "alarm_content_index")
public class AlarmContentIndex {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	@Field(type = FieldType.Text, analyzer = "korean")
	private String name;

	@Field(type = FieldType.Text, analyzer = "korean")
	private String coreData;

	@Field(type = FieldType.Date, format = DateFormat.date_time)
	private LocalDateTime createdAt;

	@Field(type = FieldType.Object)
	private Attribute attributes;

	@Document(indexName = "product_attribute_index")
	static class Attribute{

	}
}
