package com.sf.honeymorning.auth.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sf.honeymorning.alarm.entity.Alarm;
import com.sf.honeymorning.alarm.repository.AlarmRepository;
import com.sf.honeymorning.auth.repository.RefreshTokenRepository;
import com.sf.honeymorning.auth.util.JWTUtil;
import com.sf.honeymorning.domain.user.dto.response.UserDetailDto;
import com.sf.honeymorning.exception.user.DuplicateException;
import com.sf.honeymorning.exception.user.UserNotFoundException;
import com.sf.honeymorning.user.constant.LoginMessage;
import com.sf.honeymorning.user.dto.CustomUserDetails;
import com.sf.honeymorning.user.entity.User;
import com.sf.honeymorning.user.repository.UserRepository;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final JWTUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final AlarmRepository alarmRepository;

	public UserDetailDto getUserInfo() {
		User loginUser = getLoginUser();
		if (loginUser == null) {
			return null;
		}

		return UserDetailDto.builder()
			.id(loginUser.getId())
			.role(loginUser.getRole())
			.email(loginUser.getEmail())
			.username(loginUser.getUsername())
			.createdAt(loginUser.getCreatedAt())
			.build();
	}

	public User getLoginUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!isLogin(principal)) {
			throw new UserNotFoundException("현재 로그인한 유저가 없습니다.");
		}
		String email = ((CustomUserDetails)principal).getUsername();
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException(
				LoginMessage.WRONG_LOGIN_REQUEST.getValue()));
	}

	private boolean isLogin(Object principal) {
		return principal instanceof CustomUserDetails;
	}

	private String extractRefreshTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("refreshToken")) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	public void register(User user) {
		boolean isDuplicated = userRepository.existsByEmail(user.getEmail());
		if (isDuplicated) {
			throw new DuplicateException("해당 이메일을 사용하는 유저가 존재합니다.");
		}

		User newUser = User.builder()
			.email(user.getEmail())
			.username(user.getEmail())
			.password(bCryptPasswordEncoder.encode(user.getPassword()))
			.role("ROLE_USER")
			.build();

		User createdUser = userRepository.save(newUser);
		Alarm alarm = Alarm.initialize(createdUser.getId());
		alarmRepository.save(alarm);
	}

	public void reissueToken(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = extractRefreshTokenFromCookie(request);

		// refreshToken check
		if (refreshToken == null) {
			return;
		}

		// expired check
		try {
			jwtUtil.isExpired(refreshToken);
		} catch (ExpiredJwtException e) {
			//response status code
			return;
		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		String category = jwtUtil.getCategory(refreshToken);
		if (!category.equals("refresh")) {
			//response status code
			return;
		}

	}

}
