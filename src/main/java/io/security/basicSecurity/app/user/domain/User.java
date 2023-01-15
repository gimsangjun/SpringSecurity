package io.security.basicSecurity.app.user.domain;

import io.security.basicSecurity.app.common.Common;
import io.security.basicSecurity.enums.role.UserRole;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
@Entity
@Table(name = "USERS")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Common implements Serializable {

    @Column(nullable = false, unique = true , length = 50)
    private String email;

    @Setter
    @Column(nullable = false)
    // MyUserDetails와 getPassword()가 겹침.
    private String pw;

    @Setter
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UserRole role;

}
