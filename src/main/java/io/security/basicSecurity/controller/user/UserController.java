package io.security.basicSecurity.controller.user;

import io.security.basicSecurity.domain.Account;
import io.security.basicSecurity.domain.AccountDto;
import io.security.basicSecurity.service.UserService;
import io.security.basicSecurity.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    // @Autowird대신 @RequiredArgsConstrictor
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping(value = "/mypage")
    public String myPage() throws Exception{
        return "user/mypage";
    }

    @GetMapping("/users")
    public String createUser(){
        return "user/login/register";
    }

    @PostMapping("/users")
    public String createUser(AccountDto accountDto){

        ModelMapper modelMapper = new ModelMapper();
        // modelmapper로 Dto를 전환.
        Account account = modelMapper.map(accountDto, Account.class);
        account.setPassword( passwordEncoder.encode(account.getPassword()) );
        userService.createUser(account);

        return "redirect:/";
    }

}
