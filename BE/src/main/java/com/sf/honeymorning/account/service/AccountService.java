package com.sf.honeymorning.account.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.account.dto.request.AccountSignUpRequest;
import com.sf.honeymorning.alarm.entity.Alarm;
import com.sf.honeymorning.alarm.repository.AlarmRepository;
import com.sf.honeymorning.exception.model.BusinessException;
import com.sf.honeymorning.exception.model.ErrorProtocol;
import com.sf.honeymorning.user.entity.User;
import com.sf.honeymorning.user.entity.UserRole;
import com.sf.honeymorning.user.repository.UserRepository;

@Transactional(readOnly = true)
@Service
public class AccountService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final AlarmRepository alarmRepository;

	public AccountService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
		AlarmRepository alarmRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.alarmRepository = alarmRepository;
	}

	public void create(AccountSignUpRequest accountSignUpRequest) {
		if (userRepository.existsByUsername(accountSignUpRequest.username())) {
			throw new BusinessException("중복된 이메일로 회원가입을 할 수 없어요", ErrorProtocol.POLICY_VIOLATION);
		}

		User user = userRepository.save(
			new User(accountSignUpRequest.username(),
				passwordEncoder.encode(accountSignUpRequest.rawPassword()),
				accountSignUpRequest.nickName(),
				UserRole.ROLE_USER));
		alarmRepository.save(Alarm.initialize(user.getId()));
	}
}
