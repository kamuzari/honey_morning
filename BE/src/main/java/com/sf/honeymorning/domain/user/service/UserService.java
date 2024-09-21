package com.sf.honeymorning.domain.user.service;

import com.sf.honeymorning.domain.auth.repository.RefreshTokenRepository;
import com.sf.honeymorning.domain.auth.service.AuthService;
import com.sf.honeymorning.domain.user.repository.UserRepository;
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

//    public void deleteUser(HttpServletRequest request){
//        User user = authService.getLoginUser();
//        // 쿠키 값 확인
//        Cookie[] cookies = request.getCookies();
//        String refresh = null;
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (cookie.getName().equals("refresh")) {
//                    refresh = cookie.getValue();
//                }
//            }
//        }
//        System.out.println(refresh);
//        refreshTokenRepository.deleteById(refresh);
//
//        //Refresh 토큰 Cookie 값 0
//        Cookie cookie = new Cookie("refresh", null);
//        cookie.setMaxAge(0);
//        cookie.setPath("/");
//
//        userRepository.deleteById(user.getId());
//    }
}
