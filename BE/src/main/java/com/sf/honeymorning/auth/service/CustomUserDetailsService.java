package com.sf.honeymorning.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sf.honeymorning.domain.user.entity.User;
import com.sf.honeymorning.user.dto.CustomUserDetails;
import com.sf.honeymorning.user.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User userData = userRepository.findByEmail(email)
			.orElseThrow(
				() -> new UsernameNotFoundException("요청한 이메일에 해당하는 유저가 없습니다: " + email));

		return new CustomUserDetails(userData);
	}
}

