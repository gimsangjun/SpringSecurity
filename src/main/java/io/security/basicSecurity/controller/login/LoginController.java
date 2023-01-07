package io.security.basicSecurity.controller.login;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(){
        return "user/login/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){

        // 이미 로그인되어있기 때문에(인증되어있기때문에), 인증객체가 securityContext안에 있다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null){
            // 실제로 logout필터에서 이 객체를 활용해서 로그아웃 처리를 하고있음.
            new SecurityContextLogoutHandler().logout(request,response,authentication);
        }
        return "redirect:/login";
    }

}
