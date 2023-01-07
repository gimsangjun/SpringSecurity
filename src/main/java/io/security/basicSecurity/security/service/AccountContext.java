package io.security.basicSecurity.security.service;

import io.security.basicSecurity.domain.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

// UserDetails라는 인터페이스를 구현한 User를 상속받아서 구현한다.
public class AccountContext extends User {

    // 나중에 참조할수 있도록 멤버변수로
    private final Account account;

    public AccountContext(Account account, Collection<? extends GrantedAuthority> authorities) {
        super(account.getUsername(), account.getPassword(), authorities);
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }
}
