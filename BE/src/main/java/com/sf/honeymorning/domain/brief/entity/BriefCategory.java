package com.sf.honeymorning.domain.brief.entity;

import com.sf.honeymorning.domain.common.entity.BaseEntity;
import com.sf.honeymorning.domain.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BriefCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brief_category_id")
    private Long id;

    @JoinColumn(name = "brief_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Brief brief;

    @JoinColumn(name = "tag_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tag tag;

}
