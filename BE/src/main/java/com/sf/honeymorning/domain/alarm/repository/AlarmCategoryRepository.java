package com.sf.honeymorning.domain.alarm.repository;

import com.sf.honeymorning.domain.alarm.entity.AlarmCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmCategoryRepository extends JpaRepository<AlarmCategory, Long> {
}
