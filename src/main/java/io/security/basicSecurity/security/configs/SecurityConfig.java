package io.security.basicSecurity.security.configs;

import io.security.basicSecurity.security.handler.CustomAccessDeniedHandler;
import io.security.basicSecurity.security.provider.CustomAuthenticationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;


@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        String password = passwordEncoder().encode("1111");
//        // DB에 저장하는것이 아닌 메모리에 저장. 테스트용도
//        auth.inMemoryAuthentication().withUser("user").password(password).roles("USER");
//        // ADMIN이 더 높은 사용자 권한이라는것을 모름, 그냥 roles은 그냥 단순한 문자열임. 그래서 리스트 형태로 넣어주는거임.
//        // 나중에 계층적으로 role을 설정하는것을 할거임.
//        auth.inMemoryAuthentication().withUser("manager").password(password).roles("USER","MANAGER");
//        auth.inMemoryAuthentication().withUser("admin").password(password).roles("USER","MANAGER","ADMIN");
//    }


    // 커스텀한 UserDetailsService 인데, CustomUserDetailsService에서 @Service("userDetailsService")  이런식으로 Bean으로 등록했기때문에 이런식으로 가능한듯.
    // @Autowired
    // UserDetailsService userDetailsService;


    @Autowired // 인증객체에 추가정보에 담고 싶을때 사용
    private AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource;
    @Autowired // 로그인 인증후 어떻게 할것인지 커스텀
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @Autowired
    private AuthenticationFailureHandler customAuthenticationFailureHandler;
    // 내가만든 인증처리 방법을 등록
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 내가 만든 UserDetailsServices를 사용하라고 알려주는것.
        // auth.userDetailsService(userDetailsService); // Provider에서 사용하기 때문에 주석.
        auth.authenticationProvider(authenticationProvider()); // 내가 만든 Provider사용
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        return new CustomAuthenticationProvider();
    }

    // Web ignoring설정.
    // 전역적으로 사용되는 js ,css, image등의 파일들에 대해서 보안 필터를 적용하지 않기 위한 설정
    // 아예 보안필터를 거치지 않음.
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/","/users", "user/login/**","/login*").permitAll()
                .antMatchers("/mypage").hasRole("USER")
                .antMatchers("/messages").hasRole("MANAGER")
                .antMatchers("/config").hasRole("ADMIN")
                .anyRequest().authenticated()

        .and()
                .formLogin()
                .loginPage("/login")
                // 로그인 페이지 내에서 실제 로그인 처리 실행 경로를 의미
                // 스프링 시큐리티 기본 로그인 실행 경로는 /login 이므로, 로그인 커스텀페이지를 줄경우 따로 처리해야함.
                .loginProcessingUrl("/login_proc")
                // 내가 만든 authenticationDetailsSource 등록
                .authenticationDetailsSource(authenticationDetailsSource)
                 //.defaultSuccessUrl("/")
                // 내가 만든 customAuthenticationSuccessHandler 등록
                .successHandler(customAuthenticationSuccessHandler)
                // 내가 만든 customAuthenticationFailureHandler 등록
                .failureHandler(customAuthenticationFailureHandler)
                .permitAll()
        .and()
                // 내가 만든 customAccessDeniedHandler 등록
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler());

        ;
    }

    @Bean // 커스텀 인증거부 처리 Access Denied
    public AccessDeniedHandler accessDeniedHandler() {
        CustomAccessDeniedHandler accessDeniedHandler = new CustomAccessDeniedHandler();
        accessDeniedHandler.setErrorPage("/denied");
        return accessDeniedHandler;
    }

}
