package kr.co.onehunnit.onhunnit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.config.jwt.JwtTokenProvider;
import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.account.Provider;
import kr.co.onehunnit.onhunnit.domain.account.Status;
import kr.co.onehunnit.onhunnit.domain.global.Role;
import kr.co.onehunnit.onhunnit.dto.account.AccountRequestDto;
import kr.co.onehunnit.onhunnit.dto.account.TokenAccountInfoDto;
import kr.co.onehunnit.onhunnit.repository.AccountRepository;
import kr.co.onehunnit.onhunnit.repository.CaregiverRepository;
import kr.co.onehunnit.onhunnit.repository.PatientRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

	private final AccountRepository accountRepository;
	private final PatientRepository patientRepository;
	private final CaregiverRepository caregiverRepository;
	private final JwtTokenProvider jwtTokenProvider;

	public Long signUp(AccountRequestDto.SignUp requestDto) {
		String email = requestDto.getEmail();
		Provider provider = Provider.valueOf(requestDto.getProvider());
		Account account = accountRepository.findByEmailAndProvider(email, provider)
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_EMAIL));

		account.signUp(requestDto);
		account.updateStatus(Status.REGISTER_INFO_PENDING);
		return account.getId();
	}

	public Account getAccountByToken(String accessToken) {
		TokenAccountInfoDto.TokenInfo tokenInfoDto = jwtTokenProvider.extractTokenInfoFromJwt(accessToken);
		String email = tokenInfoDto.getEmail();
		Provider provider = Provider.valueOf(tokenInfoDto.getProvider());
		return accountRepository.findByEmailAndProvider(email, provider)
			.orElseThrow(() -> new ApiException(ErrorCode.NO_TOKEN_ACCOUNT));
	}

	public TokenAccountInfoDto getAccessTokenInfo(String accessToken) {
		Account account = getAccountByToken(accessToken);
		Role role = getRole(account);
		return TokenAccountInfoDto.builder().account(account).role(role).build();
	}

	private Role getRole(Account account) {
		if (patientRepository.existsByAccount_Id(account.getId())) {
			return patientRepository.findByAccount_Id(account.getId()).get();
		}
		if (caregiverRepository.existsByAccount_Id(account.getId())) {
			return caregiverRepository.findByAccount_Id(account.getId()).get();
		}
		throw new ApiException(ErrorCode.NOT_EXIST_ACCOUNT);
	}

}
