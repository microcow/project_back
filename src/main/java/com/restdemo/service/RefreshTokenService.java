package com.restdemo.service;

import com.restdemo.domain.RefreshToken;
import com.restdemo.mapper.RefreshTokenMapper;
import com.restdemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    RefreshTokenMapper refreshTokenMapper;

    @Autowired
    UserMapper userMapper;


    public RefreshToken createRefreshToken(String username){
        /*RefreshToken refreshToken = RefreshToken.builder()
                .userInfo(userRepository.findByUsername(username))
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000)) // set expiry of refresh token to 10 minutes - you can configure it application.properties file
                .build();*/
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userMapper.readUser(username));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(600000));
        refreshTokenMapper.createRefreshToken(refreshToken);

        return refreshToken;
    }



    public RefreshToken findByToken(String token){
        return refreshTokenMapper.findByToken(token);
    }

    public void verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenMapper.deleteRefreshToken(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
    }
}
