package io.security.basicSecurity.app.user.service;

import io.security.basicSecurity.app.user.domain.User;
import io.security.basicSecurity.app.user.dto.SignUpDTO;
import io.security.basicSecurity.app.user.repository.UserRepository;
import io.security.basicSecurity.enums.role.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional // TODO : 이것의 의미는?
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional // TODO : 이것의 의미는?
    public User signUp(final SignUpDTO signUpDTO){
        final User user = User.builder()
                .email(signUpDTO.getEmail())
                .pw(passwordEncoder.encode(signUpDTO.getPassword()))
                .role(UserRole.ROLE_USER) // 기본적으로 ROLE_USER권한으로
                .build();
        return userRepository.save(user);
    }

    public boolean isEmailDuplicated(final String email){
        return userRepository.existsByEmail(email);
    }

    public List<User> findAll() {
        return  userRepository.findAll();
    }


}
