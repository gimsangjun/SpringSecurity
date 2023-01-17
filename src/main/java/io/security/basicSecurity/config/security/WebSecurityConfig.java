package io.security.basicSecurity.config.security;

import io.security.basicSecurity.config.filter.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity // 기본적인 web보안을 활성화 하겠다는 의미
@EnableGlobalMethodSecurity(prePostEnabled = true) // 이것의 의미는?
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsService jwtUserDetailsService; // 이렇게 이름을 지을경우 JwtUserDetailsService를 가져오나?
    private final JwtRequestFilter jwtRequestFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 아래의 요청들은 Spring security 로직을 수행하지 않도록 모두 무시하도록 설정.
        web
                .ignoring()
                .antMatchers(
                        "/h2-console/**"
                        ,"/favicon.ico"
                );
    }

    @Bean
    @Override // 의문 : 이게 왜 필요한지? -> 먼저, AuthenticationManager 를 외부에서 사용 하기 위해
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                // 아래의 요청들은 토큰없이도 가능하도록
                .authorizeRequests().antMatchers("/authenticate","/signUp").permitAll()
                // 위를 제외한 다른 요청들은 인증이 필요
                .anyRequest().authenticated()
                .and()
                // exception을 핸들링하기 위함. 조금더 자세한 공부 필요
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                // 토큰을 활용하면 세션이 필요 없으므로 STATELESS로 설젛아여 Session을 사용하지 않는다.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // form 기반의 로그인에 대해 비활성화 한다.
                .formLogin()
                    .disable()
                // 모든 리퀘스트마다 토큰을 유효성 검사하기위해 필터 추가.
                // 의문 : 인증필터를 따로 넣으면 UsernamePassowrdAuthencationFilter는 그러면 작동하는가?
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }


}
