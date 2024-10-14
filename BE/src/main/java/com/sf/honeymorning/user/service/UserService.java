package com.sf.honeymorning.user.service;

import org.springframework.stereotype.Service;

import com.sf.honeymorning.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public Boolean validateEmail(String email) {
		return userRepository.existsByUsername(email);
	}

}
