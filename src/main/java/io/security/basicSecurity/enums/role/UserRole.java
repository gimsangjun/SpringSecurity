package io.security.basicSecurity.enums.role;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // TODO 이건 왜 있는걸가?
@Getter
public enum UserRole {

    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private final String value;

}
