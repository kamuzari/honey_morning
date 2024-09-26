package com.sf.honeymorning.domain.auth.service;

import com.sf.honeymorning.domain.alarm.entity.Alarm;
import com.sf.honeymorning.domain.alarm.repository.AlarmRepository;
import com.sf.honeymorning.domain.auth.repository.RefreshTokenRepository;
import com.sf.honeymorning.domain.auth.util.JWTUtil;
import com.sf.honeymorning.domain.tag.repository.TagRepository;
import com.sf.honeymorning.domain.user.constant.LoginMessage;
import com.sf.honeymorning.domain.user.dto.CustomUserDetails;
import com.sf.honeymorning.domain.user.dto.response.UserDetailDto;
import com.sf.honeymorning.domain.user.entity.User;
import com.sf.honeymorning.domain.user.repository.UserRepository;
import com.sf.honeymorning.exception.user.DuplicateException;
import com.sf.honeymorning.exception.user.UserNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AlarmRepository alarmRepository;
    private final TagRepository tagRepository;

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
                .build();
    }

    public User getLoginUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!isLogin(principal)) {
            throw new UserNotFoundException("현재 로그인한 유저가 없습니다.");
        }
        String email = ((CustomUserDetails) principal).getUsername();
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

    public void saveUser(User user) {

        String email = user.getEmail();
        String username = user.getUsername();
        String password = user.getPassword();
        String role = "ROLE_USER";

        // 중복 체크
        Boolean isExist = userRepository.existsByEmail(email);

        if (isExist) {
            throw new DuplicateException("해당 이메일을 사용하는 유저가 존재합니다.");
        }

        User newUser = User.builder()
                .password(bCryptPasswordEncoder.encode(password))
                .username(username)
                .email(email)
                .role(role)
                .build();

        User savedUser = userRepository.save(newUser);

        // 알람 entity 생성
        Alarm alarm = Alarm.builder()
                .user(savedUser)
                .alarmTime(LocalTime.of(7, 0)) // 오전 7시 기본 설정
                .daysOfWeek("1111111") // 모든 요일 기본 설정
                .repeatFrequency(0) // 반복 없음 기본 설정
                .repeatInterval(0) // 반복 없음 기본 설정
                .isActive(0) // 비활성 상태 기본 설정
                .build();

        // database 저장
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

        //DB에 저장되어 있는지 확인
//        Boolean isExist = refreshTokenRepository.existsByRefresh(refreshToken);
//        if (!isExist) {
//
//            //response body
//            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
//        }

    }

}
