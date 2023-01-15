package io.security.basicSecurity.app.user.service;

import io.security.basicSecurity.app.user.domain.MyUserDetails;
import io.security.basicSecurity.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
// DB로부터 사용자 정보를 가져옴.
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                // Colletions.singleton : 단 한개의 객체만 저장가능.
                .map(u -> new MyUserDetails(u, Collections.singleton(new SimpleGrantedAuthority(u.getRole().getValue()))))
                .orElseThrow( () -> new UsernameNotFoundException(email)); // 아이디를 찾지 못했을 경우
    }
}
