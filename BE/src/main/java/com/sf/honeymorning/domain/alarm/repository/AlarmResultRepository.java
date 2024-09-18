package com.sf.honeymorning.domain.alarm.repository;

import com.sf.honeymorning.domain.alarm.entity.AlarmResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlarmResultRepository extends JpaRepository<AlarmResult, Long> {
    @Query("SELECT ar FROM AlarmResult ar WHERE ar.user.id = :userId")
    List<AlarmResult> findAllByUserId(@Param("userId") Long userId);
}
