package io.security.basicSecurity.security.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 커스텀 인증거부 처리 Access Denied, 인가예외처리가 맞는듯.
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private String errorPage;

    @Override // 인가예외가 파라미터로 전달되고있다.
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 접근할수 없다는 메세지를 페이지로 만들어서 뿌려주도록 하자.
        String deniedUrl = errorPage + "?excetpion=" + accessDeniedException.getMessage();
        response.sendRedirect(deniedUrl);
    }

    public void setErrorPage(String errorPage){
        this.errorPage = errorPage;
    }

}
