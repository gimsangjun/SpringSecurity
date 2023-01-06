package io.security.basicSecurity.service.impl;

import io.security.basicSecurity.domain.Account;
import io.security.basicSecurity.repository.UserRepository;
import io.security.basicSecurity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public void createUser(Account account) {
        userRepository.save(account);
    }
}
