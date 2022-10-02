package io.security.basicSecurity.service.impl;

import io.security.basicSecurity.domain.Account;
import io.security.basicSecurity.repository.UserRepository;
import io.security.basicSecurity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public void createUser(Account account) {

        userRepository.save(account);
    }
}
