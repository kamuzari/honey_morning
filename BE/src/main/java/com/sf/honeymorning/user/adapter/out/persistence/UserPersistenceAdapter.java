package com.sf.honeymorning.user.adapter.out.persistence;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.alarm.adapter.out.persistence.entity.AlarmEntity;
import com.sf.honeymorning.alarm.adapter.out.persistence.repository.AlarmRepository;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.common.exception.model.constant.ErrorProtocol;
import com.sf.honeymorning.user.adapter.out.persistence.entity.UserEntity;
import com.sf.honeymorning.user.adapter.out.persistence.entity.UserRole;
import com.sf.honeymorning.user.adapter.out.persistence.mapper.AccountMappers;
import com.sf.honeymorning.user.adapter.out.persistence.repository.UserRepository;
import com.sf.honeymorning.user.application.domain.AuthenticateAccount;
import com.sf.honeymorning.user.application.port.out.LoadAccountPort;
import com.sf.honeymorning.user.application.port.out.ViolateAccountPort;
import com.sf.honeymorning.user.application.port.out.WriteAccountPort;

@Transactional(readOnly = true)
@Component
class UserPersistenceAdapter implements LoadAccountPort, ViolateAccountPort, WriteAccountPort {
	private final UserRepository userRepository;
	private final AlarmRepository alarmRepository;
	private final AccountMappers accountMapper;

	public UserPersistenceAdapter(
		AccountMappers accountMapper,
		UserRepository userRepository,
		AlarmRepository alarmRepository) {

		this.accountMapper = accountMapper;
		this.userRepository = userRepository;
		this.alarmRepository = alarmRepository;
	}

	public AuthenticateAccount getAccount(String username) {
		UserEntity user = userRepository.findByUsername(username)
			.orElseThrow(() -> new BadCredentialsException("authentication error"));

		return accountMapper.toAccount(user);
	}

	public void duplicate(String username) {
		if (userRepository.existsByUsername(username)) {
			throw new BusinessException("중복된 이메일로 회원가입을 할 수 없습니다.", ErrorProtocol.POLICY_VIOLATION);
		}
	}

	public boolean isExist(String username) {
		return userRepository.existsByUsername(username);
	}

	@Transactional
	public void create(String username, String encryptedPassword, String nickName) {
		UserEntity savedUser = userRepository.save(new UserEntity(
			username,
			encryptedPassword,
			nickName,
			UserRole.ROLE_USER
		));
		alarmRepository.save(AlarmEntity.initialize(savedUser.getId()));
	}
}
