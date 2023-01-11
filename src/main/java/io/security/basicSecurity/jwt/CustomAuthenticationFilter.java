package io.security.basicSecurity.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
// TODO : 이 필터를 구현해서 추가한다면, 기존의 UsernamePassowordAuthenticationFilter는 어떻게 되는것인가? -> 디버거 걸어놓음 나중에 구현후 확인
//
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
}
