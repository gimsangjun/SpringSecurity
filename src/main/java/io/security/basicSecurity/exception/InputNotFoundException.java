package io.security.basicSecurity.exception;

// 아이디와 비밀번호가 제대로 전달되지 않았을 경우
public class InputNotFoundException extends RuntimeException{

    public InputNotFoundException(){
        super();
    }

}
