package com.sf.honeymorning.domain.auth.service;

import com.sf.honeymorning.domain.auth.repository.RefreshTokenRepository;
import com.sf.honeymorning.domain.auth.util.JWTUtil;
import com.sf.honeymorning.domain.user.constant.LoginMessage;
import com.sf.honeymorning.domain.user.dto.CustomUserDetails;
import com.sf.honeymorning.domain.user.dto.response.UserDetailDto;
import com.sf.honeymorning.domain.user.entity.User;
import com.sf.honeymorning.domain.user.repository.UserRepository;
import com.sf.honeymorning.domain.user.service.UserStatusService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final UserStatusService userStatusService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserDetailDto getUserInfo(HttpServletRequest request) {
        User loginUser = getLoginUser();
        if (loginUser == null) {
            return null;
        }

        return UserDetailDto.builder()
                .id(loginUser.getId())
                .email(loginUser.getEmail())
                .build();
    }

    public User getLoginUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!isLogin(principal)) {
            return null;
        }
        String email = ((CustomUserDetails) principal).getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        LoginMessage.WRONG_LOGIN_REQUEST.getValue()));
    }

    private boolean isLogin(Object principal) {
        return principal instanceof CustomUserDetails;
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            log.info("refreshToken을 쿠키에서 찾을 수 없습니다.");
            return;
        }

        String email = jwtUtil.getUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 이메일의 유저가 없습니다"));
        String userId = user.getId().toString();

        // refreshToken 쿠키 제거
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        refreshTokenRepository.deleteById(refreshToken);
//        userStatusService.setUserOffline(Long.parseLong(userId));
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

    public void saveUser(User user){

        String email = user.getEmail();
        String username = user.getUsername();
        String password = user.getPassword();

        // 중복 체크
        Boolean isExist = userRepository.existsByEmail(email);

        if(isExist){
            return;
        }

        User newUser = User.builder()
                .password(bCryptPasswordEncoder.encode(password))
                .username(username)
                .email(email)
                .build();

        userRepository.save(newUser);

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
