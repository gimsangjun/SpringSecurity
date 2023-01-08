package io.security.basicSecurity.security.handler;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 커스텀 인증 실패 핸들러
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override // 인증을 검사할때, 실패하게되면 인증예외를 발생하게되는데 그때 처리하기위해서 exception이 parameter로 들어온다.
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        // 클라이언트에게 예외메시지를 보여주도록 하자.
        String errorMessage = "Invalid Username or Password";

        if(exception instanceof BadCredentialsException){
            errorMessage = "Invalid Username or Password";
        }else if (exception instanceof InsufficientAuthenticationException){
            // 우리의 커스텀 AuthenticationDetails의 secret의 에러
             errorMessage = "Invalid Secret Key";
        }

        // 파라미터값으로 값을 던져주기위해, /login페이지로 다시 이동하는데 거기의 파라미터값으로 들어간다.
        setDefaultFailureUrl("/login?error=true&exception=" + exception.getMessage());

        // 부모에게 요청 위임.
        super.onAuthenticationFailure(request,response,exception);
    }
}
