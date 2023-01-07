package io.security.basicSecurity.security.service;

import io.security.basicSecurity.domain.Account;
import io.security.basicSecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// 인증을 시도하는 사용자의 id 값을 가지고 현재 시스템 계정에 존재하는지를 검증하는 비즈니스 로직을 구현하는 곳
@Service("userDetailsService") // Bean으로 설정
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Account account = userRepository.findByUsername(username);

        if(account == null) {
            throw new UsernameNotFoundException("UsernameNotFoundException");
        }

        // UserDetails 타입의 객체를 반환해야됨. UserDetailsService를 커스텀화하였기 때문에
        // UserDetails도 커스텀해야됨.
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(account.getRole()));

        AccountContext accountContext = new AccountContext(account,roles);

        return accountContext;
    }
}
