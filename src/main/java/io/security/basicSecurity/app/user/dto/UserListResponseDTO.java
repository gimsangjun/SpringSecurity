package io.security.basicSecurity.app.user.dto;

import io.security.basicSecurity.app.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder // TODO : 무슨뜻일까?
@RequiredArgsConstructor
@NoArgsConstructor(force = true) // TODO : 무슨뜻일까?
public class UserListResponseDTO {

    private final List<User> userList;

}
