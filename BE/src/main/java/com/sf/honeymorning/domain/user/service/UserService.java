package com.sf.honeymorning.domain.user.service;

import com.sf.honeymorning.domain.auth.repository.RefreshTokenRepository;
import com.sf.honeymorning.domain.auth.service.AuthService;
import com.sf.honeymorning.domain.user.entity.User;
import com.sf.honeymorning.domain.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final RefreshTokenRepository refreshTokenRepository;

    public Boolean validateEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void deleteUser(HttpServletRequest request){
        User user = authService.getLoginUser();
        System.out.println("check1");
        // 쿠키 값 확인
        Cookie[] cookies = request.getCookies();
        String refresh = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                }
            }
        }
        System.out.println("check2");
        System.out.println(refresh);
        refreshTokenRepository.deleteById(refresh);

        //Refresh 토큰 Cookie 값 0
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        System.out.println("check");
        System.out.println(user.getId());
        System.out.println(user.getUsername());
        System.out.println(user.getPassword());
        userRepository.deleteById(user.getId());
    }
}
