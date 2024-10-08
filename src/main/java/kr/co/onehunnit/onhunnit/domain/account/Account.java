package kr.co.onehunnit.onhunnit.domain.account;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.co.onehunnit.onhunnit.domain.global.BaseTimeEntity;
import kr.co.onehunnit.onhunnit.dto.account.AccountRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String email;

	@Enumerated(value = EnumType.STRING)
	private Provider provider;

	private String profile_image;

	private String name;

	private String nickname;

	private int age;

	private String phone;

	@Enumerated(value = EnumType.STRING)
	private Gender gender;

	private String birthday;

	@Enumerated(value = EnumType.STRING)
	private Status status;

	public void signUp(AccountRequestDto.SignUp requestDto) {
		this.name = requestDto.getName();
		this.age = requestDto.getAge();
		this.phone = requestDto.getPhone();
		this.gender = Gender.valueOf(requestDto.getGender());
		this.birthday = requestDto.getBirthday();
	}

	public void updateStatus(Status status) {
		this.status = status;
	}
}
