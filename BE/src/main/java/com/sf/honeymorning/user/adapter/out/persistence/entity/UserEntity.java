package com.sf.honeymorning.user.adapter.out.persistence.entity;

import com.sf.honeymorning.common.entity.basic.BaseEntity;

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
public class UserEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String username;

	private String password;

	@Column(name = "nick_name", unique = true, nullable = false)
	private String nickName;

	private Integer maximumStreak;

	@Enumerated(value = EnumType.STRING)
	private UserRole role;

	public UserEntity(String username, String password, String nickName, UserRole role) {
		this.username = username;
		this.password = password;
		this.nickName = nickName;
		this.maximumStreak = 0;
		this.role = role;
	}

	public UserEntity(Long id, String username, String password, String nickName, Integer maximumStreak,
		UserRole role) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.nickName = nickName;
		this.maximumStreak = maximumStreak;
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

	public int getMaxStreak() {
		return this.maximumStreak;
	}

	public void updateMaximumStreak(int consecutiveDays) {
		if (this.maximumStreak < consecutiveDays) {
			this.maximumStreak = consecutiveDays;
		}
	}
}
