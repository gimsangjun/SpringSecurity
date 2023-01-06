package io.security.basicSecurity.domain;

import lombok.Data;

@Data
// 클라이언트로부터 데이터를 받아노는 DTO
public class AccountDto {

    private String username;
    private String password;
    private String email;
    private String age;
    private String role;

}
