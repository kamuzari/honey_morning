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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 데이터베이스에서 특정 유저에 대한 정보를 반환
        System.out.println(username);
        System.out.println("username");
        User userData = userRepository.findByUsername(username);

        if(userData != null){
            System.out.println("check");
            return new CustomUserDetails(userData);
        }

        System.out.println("check2");
        return null;
    }
}

