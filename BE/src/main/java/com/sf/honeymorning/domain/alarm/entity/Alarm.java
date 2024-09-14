package com.sf.honeymorning.domain.alarm.entity;

import com.sf.honeymorning.domain.common.BaseEntity;
import com.sf.honeymorning.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Alarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long id;

    @JoinColumn(name = "user_id")
    @OneToOne
    private User user;

    /**
     * 한주의 알람을 비트마스킹으로 표현합니다. (총 7비트로 월요일부터 시작)
     * 예시)
     * - 1 (0000001) = Monday only
     * - 65 (1000001) = Monday and Sunday
     * - 127 (1111111) = Every day
     */
    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private String daysOfWeek = "1111111";

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer repeatFrequency = 0;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer repeatInterval = 0;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    private Integer isActive = 1;

    @Column(length = 1000, nullable = false)
    private String musicFilePath;

}
