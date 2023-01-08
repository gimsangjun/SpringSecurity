package io.security.basicSecurity.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
// DB에 저장하기 위한 entity
// 왜 직렬화(Serializable)가 있는가? 권장함. 가장 큰 목적은 원격으로 객체를 전송하기 위함.
public class Account implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password;
    private String email;
    private String age;
    private String role;

}
