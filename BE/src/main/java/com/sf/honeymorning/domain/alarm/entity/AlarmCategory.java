package com.sf.honeymorning.domain.alarm.entity;

import com.sf.honeymorning.domain.common.entity.BaseEntity;
import com.sf.honeymorning.domain.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class AlarmCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_category_id")
    private Long id;

    @JoinColumn(name = "alarm_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Alarm alarm;

    @JoinColumn(name = "tag_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tag tag;

    public AlarmCategory(Alarm alarm, Tag tag){
        this.alarm = alarm;
        this.tag = tag;
    }

}
