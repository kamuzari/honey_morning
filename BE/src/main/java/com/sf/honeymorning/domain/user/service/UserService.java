package com.sf.honeymorning.domain.user.service;

import com.sf.honeymorning.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Boolean validateEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
