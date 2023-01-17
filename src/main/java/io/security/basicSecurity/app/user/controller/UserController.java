package io.security.basicSecurity.app.user.controller;

import io.security.basicSecurity.app.user.domain.MyUserDetails;
import io.security.basicSecurity.app.user.domain.User;
import io.security.basicSecurity.app.user.dto.JwtRequest;
import io.security.basicSecurity.app.user.dto.JwtResponse;
import io.security.basicSecurity.app.user.dto.SignUpDTO;
import io.security.basicSecurity.app.user.dto.UserListResponseDTO;
import io.security.basicSecurity.app.user.service.JwtUserDetailsService;
import io.security.basicSecurity.app.user.service.UserService;
import io.security.basicSecurity.enums.role.UserRole;
import io.security.basicSecurity.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    // 회원가입 : DB에 저장할때 필요
    private final UserService userService;

    // 인증할때 필요
    private final AuthenticationManager authenticationManager;
    private final JwtUserDetailsService userDetailsService;
    // jwt 토큰 관련 도구
    private final JwtTokenUtils jwtTokenUtils;

    /** 회원가입
     *  모든 사용자가 접근 가능
     *  @return JWT Token을 만들어서 리턴.
     */
    @PostMapping(value = "/signUp")
    public ResponseEntity<String> signUp(@RequestBody final SignUpDTO signUpDTO){
        return userService.isEmailDuplicated(signUpDTO.getEmail())
                ? ResponseEntity.badRequest().build()
                : ResponseEntity.ok(jwtTokenUtils.generateToken(userService.signUp(signUpDTO)));
    }

    /** 로그인을 하면 토큰발행
     *  @return jwt토큰
     */
    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception{

        // 비밀번호 등이 틀렸는지 검증.
        authenticate(authenticationRequest.getEmail(),authenticationRequest.getPassword());

        // MyUserDetails를 리턴함.
        final MyUserDetails userDetails = (MyUserDetails) userDetailsService
                .loadUserByUsername(authenticationRequest.getEmail());

        final String token = jwtTokenUtils.generateToken(User.builder()
                .email(userDetails.getEmail())
                .pw(userDetails.getPw())
                .role(UserRole.ROLE_USER)
                .build());

        return ResponseEntity.ok(new JwtResponse(token));

    }

    private void authenticate(String email, String password) throws Exception{
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
        } catch (DisabledException e){
            throw new Exception("User_DISABLED",e);
        } catch (BadCredentialsException e){ // 비밀번호 안맞음.
            throw new Exception("INVALID_CREDENTIALS",e);
        }

    }

    // 유효한 토큰을 전송한 사용자만 접근가능. -> 인터센터에서 체크함. (WebSecurityConfig)
    @GetMapping(value = "/user/list")
    public ResponseEntity<UserListResponseDTO> findAll() {
        final UserListResponseDTO userListResponseDTO = UserListResponseDTO.builder()
                .userList(userService.findAll()).build();

        return ResponseEntity.ok(userListResponseDTO);
    }
}
