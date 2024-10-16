package com.sf.honeymorning.authentication.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sf.honeymorning.alarm.repository.AlarmRepository;
import com.sf.honeymorning.authentication.util.JWTUtil;
import com.sf.honeymorning.exception.user.UserNotFoundException;
import com.sf.honeymorning.user.dto.CustomUserDetails;
import com.sf.honeymorning.user.entity.User;
import com.sf.honeymorning.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final JWTUtil jwtUtil;
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final AlarmRepository alarmRepository;

	public User getLoginUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!isLogin(principal)) {
			throw new UserNotFoundException("현재 로그인한 유저가 없습니다.");
		}
		String email = ((CustomUserDetails)principal).getUsername();
		return userRepository.findByUsername(email)
			.orElseThrow(() -> new IllegalArgumentException(""));
	}

	private boolean isLogin(Object principal) {
		return principal instanceof CustomUserDetails;
	}

}
