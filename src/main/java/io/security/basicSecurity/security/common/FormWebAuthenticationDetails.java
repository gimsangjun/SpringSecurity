package io.security.basicSecurity.security.common;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

// 인증객체에 추가정보를 담을때 사용하는 클래스이다.
// 인증과정중 전달된 데이터를 저장하는 역할을 한다.
public class FormWebAuthenticationDetails extends WebAuthenticationDetails {

    private String secretKey;

    public FormWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        secretKey = request.getParameter("secret_key");
    }

    public String getSecretKey(){
        return secretKey;
    }
}
