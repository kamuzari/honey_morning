package com.sf.honeymorning.domain.alarm.entity;

import com.sf.honeymorning.domain.common.entity.BaseEntity;
import com.sf.honeymorning.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class AlarmResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_result_id")
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer count = 0;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer isAttending = 0;
    
}
