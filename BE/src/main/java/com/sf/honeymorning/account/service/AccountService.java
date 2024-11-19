package com.sf.honeymorning.account.service;

import static com.sf.honeymorning.account.authenticater.jwt.JwtProviderManager.CustomClaim;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.account.authenticater.constant.JwtProperty;
import com.sf.honeymorning.account.authenticater.jwt.JwtProviderManager;
import com.sf.honeymorning.account.dto.request.AccountSignUpRequest;
import com.sf.honeymorning.account.dto.request.LoginAuthRequestDto;
import com.sf.honeymorning.account.dto.response.LoginAuthResponseDto;
import com.sf.honeymorning.account.dto.response.LogoutAuthResponseDto;
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
	private final AccountMapper accountMapper;
	private final JwtProviderManager jwtProviderManager;
	private final JwtProperty jwtProperty;

	public AccountService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
		AlarmRepository alarmRepository, AccountMapper accountMapper, JwtProviderManager jwtProviderManager,
		JwtProperty jwtProperty) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.alarmRepository = alarmRepository;
		this.accountMapper = accountMapper;
		this.jwtProviderManager = jwtProviderManager;
		this.jwtProperty = jwtProperty;
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

	public boolean validateEmail(String email) {
		return userRepository.existsByUsername(email);
	}

	public LoginAuthResponseDto login(LoginAuthRequestDto loginDto) {
		User principal = userRepository.findByUsername(loginDto.username())
			.orElseThrow(() -> new BadCredentialsException("authentication error"));

		boolean isMatchCredential = passwordEncoder.matches(loginDto.password(), principal.getPassword());

		if (!isMatchCredential) {
			throw new BadCredentialsException("authentication error");
		}

		CustomClaim claim = CustomClaim.builder()
			.userId(principal.getId())
			.roles(new String[] {principal.getRole().name()})
			.build();

		String accessToken = jwtProviderManager.generateAccessToken(claim);
		String refreshToken = jwtProviderManager.generateRefreshToken(principal.getId());

		return accountMapper.toLoginResponse(accessToken, refreshToken, jwtProperty);
	}

	public LogoutAuthResponseDto logout(Long id) {
		jwtProviderManager.removeRefreshToken(id);

		return accountMapper.toLogoutResponse(jwtProperty);
	}
}
