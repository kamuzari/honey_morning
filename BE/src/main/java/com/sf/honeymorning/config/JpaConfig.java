package com.sf.honeymorning.config;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import jakarta.persistence.EntityListeners;

@EntityListeners(AuditingEntityListener.class)
@EnableJpaAuditing
public class JpaConfig {
}
