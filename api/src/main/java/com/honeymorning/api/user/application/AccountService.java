package com.honeymorning.api.user.application;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.honeymorning.api.common.security.authentication.constant.JwtProperty;
import com.honeymorning.api.user.adapter.in.web.dto.request.AccountSignUpRequest;
import com.honeymorning.api.user.adapter.in.web.dto.request.LoginAuthRequestDto;
import com.honeymorning.api.user.adapter.in.web.dto.response.LoginAuthResponseDto;
import com.honeymorning.api.user.adapter.in.web.dto.response.LogoutAuthResponseDto;
import com.honeymorning.api.user.application.port.in.AuthenticateUseCase;
import com.honeymorning.api.user.application.port.in.SignUpUseCase;
import com.honeymorning.api.user.application.port.out.LoadAccountPort;
import com.honeymorning.api.user.application.port.out.TokenGeneratePort;
import com.honeymorning.api.user.application.port.out.ViolateAccountPort;
import com.honeymorning.api.user.application.port.out.WriteAccountPort;

@Transactional(readOnly = true)
@Service
public class AccountService implements SignUpUseCase, AuthenticateUseCase {

	private final ViolateAccountPort violateAccountPort;
	private final LoadAccountPort loadAccountPort;
	private final WriteAccountPort writeAccountPort;
	private final PasswordEncoder passwordEncoder;
	private final TokenGeneratePort tokenGeneratePort;

	private final AccountServiceMapper accountServiceMapper;

	private final JwtProperty jwtProperty;

	public AccountService(ViolateAccountPort violateAccountPort,
		LoadAccountPort loadAccountPort,
		WriteAccountPort writeAccountPort,
		PasswordEncoder passwordEncoder,
		AccountServiceMapper accountServiceMapper,
		TokenGeneratePort tokenGeneratePort,
		JwtProperty jwtProperty) {
		this.violateAccountPort = violateAccountPort;
		this.loadAccountPort = loadAccountPort;
		this.writeAccountPort = writeAccountPort;
		this.accountServiceMapper = accountServiceMapper;
		this.passwordEncoder = passwordEncoder;
		this.tokenGeneratePort = tokenGeneratePort;
		this.jwtProperty = jwtProperty;
	}

	public boolean isUsable(String email) {
		return !violateAccountPort.isExist(email);
	}

	@Transactional
	public void register(AccountSignUpRequest requestDto) {
		violateAccountPort.duplicate(requestDto.username());
		writeAccountPort.create(requestDto.username(),
			passwordEncoder.encode(requestDto.rawPassword()),
			requestDto.nickName());
	}

	public LoginAuthResponseDto login(LoginAuthRequestDto loginDto) {
		var authenticateAccount = loadAccountPort.getAccount(loginDto.username());

		boolean isMatchCredential = passwordEncoder.matches(
			loginDto.password(),
			authenticateAccount.encryptedPassword()
		);
		if (!isMatchCredential) {
			throw new BadCredentialsException("authentication error");
		}

		var accessToken = tokenGeneratePort.generateAccessToken(authenticateAccount.id(), authenticateAccount.role());
		var refreshToken = tokenGeneratePort.generateRefreshToken(authenticateAccount.id());

		return accountServiceMapper.toLoginResponse(accessToken, refreshToken, jwtProperty);
	}

	public LogoutAuthResponseDto logout(Long id) {
		tokenGeneratePort.removeRefreshToken(id);

		return accountServiceMapper.toLogoutResponse(jwtProperty);
	}
}
