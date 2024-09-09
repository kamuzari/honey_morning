package com.sf.honeymorning.domain.auth.service;

import com.sf.honeymorning.domain.user.dto.CustomUserDetails;
import com.sf.honeymorning.domain.user.entity.User;
import com.sf.honeymorning.domain.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("check22");
        // 데이터베이스에서 특정 유저에 대한 정보를 반환
        User userData = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new UsernameNotFoundException("요청한 이메일에 해당하는 유저가 없습니다: " + email));

        return new CustomUserDetails(userData);
    }
}

