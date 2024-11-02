package com.restdemo.mapper;
import com.restdemo.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Mapper
public interface UserMapper {
    //유저읽기
    public User readUser(String username);

    //유저생성
    public void createUser(User user);

    // 권한 읽기
    public List<GrantedAuthority> readAuthorities(String username);

    // 권한 생성
    public void createAuthority(User user);
    
    // 유저 삭제
    public void deleteUser(String username);
    
    // 유저 리스트 읽기
    public List<User> readUserList();
    
    // 유저 정보 업데이트
    public void updateUser(User user);
    
    // 유저 권한 업데이트
    public void updateAuth(User user);
}

