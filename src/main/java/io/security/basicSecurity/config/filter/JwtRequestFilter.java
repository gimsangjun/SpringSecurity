package io.security.basicSecurity.config.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.security.basicSecurity.app.user.domain.MyUserDetails;
import io.security.basicSecurity.app.user.service.JwtUserDetailsService;
import io.security.basicSecurity.constants.AuthConstants;
import io.security.basicSecurity.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
/**
 * 어떤 request든지 이 클래스가 실행됨.
 * JWT token이 있는지 확인함.
 */
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUserDetailsService jwtUserDetailsService;

    private final JwtTokenUtils jwtTokenUtils;

    // TODO: 모든 request가 이 필터를 통과해서, 토큰이 필요 없는 URL요청에도 통과됨.
    // TODO: 인증이 필요한 URL에 요청을 할 때,  인증이 없으면 오류메시지를 보내야되는데 어디서 처리해야하는지 모르겠음/
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // Postman에서는 Key: "Authorization" , Value : "Bearer 토큰값" 임.
        final String requestTokenHeader = request.getHeader(AuthConstants.AUTH_HEADER);

        String email = null;
        String jwtToken = null;

        log.info("요청 URL={}",request.getRequestURL());

        // "Authorization" 헤더가 있고, 헤더 Value가 Bearer로 시작하면
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")){
            // Value : "Bearer 토큰값"에서 Bearer를 자르고 토큰값만
            jwtToken = JwtTokenUtils.getTokenFromHeader(requestTokenHeader);
            // TODO : throw가 없는데 error가 발생하는가? -> 원래 참고했던 사이트는 어떻게? https://mangkyu.tistory.com/55
            try{
                email = jwtTokenUtils.getEmailFromToken(jwtToken);
            } catch (IllegalArgumentException e){
                log.info("Unable to get JWT Token");
            } catch (ExpiredJwtException e){
                log.info("JWT Token has expired");
            }
        } else{
            log.error("JWT Token does not begin with Bearer String");
        }

        // 토큰을 받으면 유효성 확인
        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){ // email이 있고, SecurityContextHolder에 인증객체가 없을 때
            MyUserDetails userDetails = (MyUserDetails) this.jwtUserDetailsService.loadUserByUsername(email);

            if (jwtTokenUtils.validateToken(jwtToken,userDetails)){ // TODO: va
                // 인증 객체 생성
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 인증 정보를 저장.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }

        }
        chain.doFilter(request,response);
    }
}
