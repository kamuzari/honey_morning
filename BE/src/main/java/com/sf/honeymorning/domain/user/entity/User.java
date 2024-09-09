package com.sf.honeymorning.domain.user.entity;

import com.sf.honeymorning.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(length = 50, nullable = false)
    private String username;

    // TODO : type, role, isActive 필드도 고려사항
    private String role;

    @Builder
    public User(String email, String password, String username, String role){
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
    }


}
