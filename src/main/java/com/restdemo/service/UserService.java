package com.restdemo.service;

import com.restdemo.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;

public interface UserService extends UserDetailsService {
    //유저읽기
    public User readUser(String username);

    //유저생성
    public void createUser(User user);

    // 권한 생성
    public void createAuthorities(User user);

    // 시큐리티 권한 얻기
    Collection<GrantedAuthority> getAuthorities(String username);

}
