package com.sf.honeymorning.domain.auth.filter;

import com.sf.honeymorning.domain.auth.entity.RefreshToken;
import com.sf.honeymorning.domain.auth.repository.RefreshTokenRepository;
import com.sf.honeymorning.domain.auth.util.JWTUtil;
import com.sf.honeymorning.domain.user.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
                       RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/auth/login");
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse res) throws AuthenticationException {
        // 내부의 요청을 가로채서 요청 내에 있는 아이디와 패스워드를 추출할 것이다.
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        System.out.println(username);
        System.out.println(password);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);


        return authenticationManager.authenticate(authToken);
    }

    @Override
    public void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication authentication){

        System.out.println("check4");

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        // 유저 정보
        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // access 토큰 생성 및 발급
        String access = jwtUtil.createAccessJwt("access", username, role);
        res.setHeader("access", access);

        // refresh 토큰 생성 및 발급
        String refresh = jwtUtil.createRefreshJwt("refresh", username, role);
        res.addCookie(createCookie("refresh", refresh));

        // Refresh 토큰 저장 (oracle)
//        addRefreshEntity(username, refresh, 864000000L);

        // Refresh 토큰 저장 (redis)
        RefreshToken redis = new RefreshToken(refresh, customUserDetails.getUsername());

        // Refresh 토큰 저장 (repository)
        refreshTokenRepository.save(redis);
        // user 상태 저장
//        userStatusService.setUserOnline(customUserDetails.getUser().getId());

        // 응답 설정
        res.setStatus(HttpStatus.OK.value());
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

}
