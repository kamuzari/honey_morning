package com.sf.honeymorning.domain.tag.entity;

import com.sf.honeymorning.domain.common.entity.BaseEntity;
import com.sf.honeymorning.domain.user.entity.User;
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

    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne
    private User user;

    @Column(length = 50, nullable = false)
    private String word;

    /**
     * 커스텀 태그인지 확인합니다.
     * 0일 경우 커스텀하지 않은 태그이며, 1일 경우 커스텀한 태그입니다.
     */
    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    private Integer isCustom;

    /**
     * 사용자가 해당 태그를 선택했는지 확인합니다.
     * 0일 경우 선택하지 않은 태그이며, 1일 경우 선택한 태그입니다.
     */
    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    private Integer isSelected;

}
