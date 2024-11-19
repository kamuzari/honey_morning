package com.sf.honeymorning.user.entity;

import com.sf.honeymorning.common.entity.BaseEntity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Table(name = "users")
@AttributeOverride(name = "createdAt", column = @Column(name = "signup_at"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String username;

	private String password;

	private String nickName;

	@Enumerated(value = EnumType.STRING)
	private UserRole role;

	public User(String username, String password, String nickName, UserRole role) {
		this.username = username;
		this.password = password;
		this.nickName = nickName;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getNickName() {
		return nickName;
	}

	public UserRole getRole() {
		return role;
	}


}
