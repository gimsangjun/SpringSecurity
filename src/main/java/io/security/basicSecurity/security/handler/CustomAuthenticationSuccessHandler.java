package io.security.basicSecurity.security.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 로그인 성공 후에, 어떤 부가적인 작업들을 더 해야되는 경우에는
// 스프링 시큐리티의 기본 인증 성공 핸들러만 사용할 수 없다.
// 그래서 내가 커스텀화 한다.
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // 이전에 사용자가 요청했던 관련된 객체를 참조해서 사용할거임.
    private RequestCache requestCache = new HttpSessionRequestCache();

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        // 사용자가 인증에 성공하기전에 요청을 했던 정보들을 가지고 있는 객체
        SavedRequest savedRequest = requestCache.getRequest(request,response);

        if(savedRequest != null){
            // 인증을 받기전에 사용자가 가고자 했던 URL로 이동시키게 만든 기능.
            String targetUrl = savedRequest.getRedirectUrl();
            redirectStrategy.sendRedirect(request,response,targetUrl);
        }else { //  그냥 바로 로그인성공을했을경우, savedRequest객체가 null일수 있다.
            redirectStrategy.sendRedirect(request,response,"/");
        }

    }
}
