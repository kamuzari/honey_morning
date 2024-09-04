package com.sf.honeymorning.domain.alarm.entity;

import com.sf.honeymorning.domain.common.BaseEntity;
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
public class AlarmMusic extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_music_id")
    private Long id;

    @JoinColumn(name = "alarm_id")
    @OneToOne
    private Alarm alarm;

    @Column(length = 1000, nullable = false)
    private String musicFilePath;

}
