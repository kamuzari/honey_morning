package com.sf.honeymorning.alarm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sf.honeymorning.alarm.entity.AlarmResult;
import com.sf.honeymorning.user.entity.User;

public interface AlarmResultRepository extends JpaRepository<AlarmResult, Long> {
	List<AlarmResult> findByUser(User user);

	List<AlarmResult> findByUserOrderByCreatedAt(User user);
}
