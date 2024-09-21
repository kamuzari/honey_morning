package com.sf.honeymorning.domain.tag.entity;

import com.sf.honeymorning.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@Builder
public class Tag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String word;

    /**
     * 커스텀 태그인지 확인합니다.
     * 0일 경우 커스텀하지 않은 태그이며, 1일 경우 커스텀한 태그입니다.
     */
    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    private Integer isCustom;

}
