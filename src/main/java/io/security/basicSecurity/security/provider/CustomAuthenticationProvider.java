package io.security.basicSecurity.security.provider;

import io.security.basicSecurity.security.common.FormAuthenticationDetailsSource;
import io.security.basicSecurity.security.common.FormWebAuthenticationDetails;
import io.security.basicSecurity.security.service.AccountContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

// 실제 인증 작업을 실행하는 첫 시작점
// ID검증과 Password검증, 우리의 입맛대로 추가검증을하여 인증처리를 할수있다.
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override // 인증처리 기능을 담당
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        // 만약 유저를 못찾으면 UserNotFoundException
        String username = authentication.getName();
        String password = (String)authentication.getCredentials();

        AccountContext accountContext = (AccountContext)userDetailsService.loadUserByUsername(username);

        // 비밀번호가 일치하지 않으면 BadCredentialException
        if(passwordEncoder.matches(password,accountContext.getPassword())){
            throw new BadCredentialsException("BadCredentialsException");
        }

        // 우리가 인증객체에 추가한 details(부가정보보)를 검증하는코드 추가.
        FormWebAuthenticationDetails formWebAuthenticationDetails = (FormWebAuthenticationDetails) authentication.getDetails();
        String secretKey = formWebAuthenticationDetails.getSecretKey();
        if(secretKey == null || !"secret".equals(secretKey)){
            throw new InsufficientAuthenticationException("InsufficientAuthenticationException");
        }

        // ID, PW 모두 성공 시
        // 사용자 정보와 권한 정보등을 담은 인증객체를 생성 및 반환(AutnenticationManager에게)
        return new UsernamePasswordAuthenticationToken(accountContext.getAccount(),null,accountContext.getAuthorities());
    }

    // `AuthenticationManager` 자체는 인터페이스, 구현체는 `ProviderManager`이다.
    // `AuthenticationProvider` 목록중에서 인증처리 요건에 맞는 `AuthenticationProvider`를 찾아 인증처리를 위임.
    @Override // 토큰(인증객체)의 타입이 일치하는지(지원하는지)
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
