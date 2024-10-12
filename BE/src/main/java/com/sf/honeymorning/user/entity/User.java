package com.sf.honeymorning.user.entity;

import com.sf.honeymorning.domain.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Table(name = "users")
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
	public User(Long id, String email, String password, String username, String role) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.username = username;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public String getRole() {
		return role;
	}
}
