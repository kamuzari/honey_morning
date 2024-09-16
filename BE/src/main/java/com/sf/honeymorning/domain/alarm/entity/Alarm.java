package com.sf.honeymorning.domain.alarm.entity;

import com.sf.honeymorning.domain.common.BaseEntity;
import com.sf.honeymorning.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@AllArgsConstructor
public class Alarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long id;

    @JoinColumn(name = "user_id")
    @OneToOne
    private User user;

    /**
     * 알람 시간이 저장되는 컬럼입니다.
     * 예시)
     * LocalTime.of(14, 23) = 14시 23분
     */
    @Column(nullable = false)
    private LocalTime alarmTime;

    /**
     * 한주의 알람을 비트마스킹으로 표현합니다. (총 7비트로 월요일부터 시작)
     * 예시)
     * - 0000001 = Monday only
     * - 1000001 = Monday and Sunday
     * - 1111111 = Every day
     */
    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 1111111")
    private Integer daysOfWeek = 1111111;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer repeatFrequency = 0;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer repeatInterval = 0;

    /**
     * 알람 활성 여부를 0과 1로 표시합니다.
     * 예시)
     * - 0 = 비활성
     * - 1 = 활성
     */
    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer isActive = 0;

    @Column(length = 1000, nullable = false)
    private String musicFilePath;

}
