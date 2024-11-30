package com.sf.honeymorning.tag.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.sf.honeymorning.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class Tag extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tag_id")
	private Long id;

	@Column(length = 50, nullable = false)
	private String word;

	/**
	 * 커스텀 태그인지 확인합니다. 0일 경우 커스텀하지 않은 태그이며, 1일 경우 커스텀한 태그입니다.
	 */
	@Column(nullable = false, columnDefinition = "INTEGER DEFAULT 1")
	private Integer isCustom;

}
