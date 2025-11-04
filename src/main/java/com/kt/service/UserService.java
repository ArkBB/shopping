package com.kt.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

import com.kt.domain.User;
import com.kt.dto.UserCreateRequest;
import com.kt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	public void create(UserCreateRequest request) {
		System.out.println(request.toString());
		var newUser = new User(
				userRepository.selectMaxId() + 1,
				request.loginId(),
				request.password(),
				request.name(),
				request.email(),
				request.mobile(),
				request.gender(),
				request.birthday(),
				LocalDateTime.now(),
				LocalDateTime.now()
		);

		userRepository.save(newUser);
		//repository로 넘길거임
	}

    public boolean isDuplicateLoginId(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    public void changePassword(String loginId , String oldPassword, String password) {

        // 존재하는 회원인지 체크
        if(!userRepository.existsByLoginId(loginId)) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
        // 기존 비밀번호와 동일한 비밀번호인지 체크
        if(oldPassword.equals(password)) {
            throw new IllegalArgumentException("기존 비밀번호와 동일한 비밀번호로 변경할 수 없습니다.");
        }

        userRepository.updatePassWordById(loginId, password);

    }
}
