package com.restdemo.service;

import com.restdemo.domain.RefreshToken;
import com.restdemo.domain.RefreshTokenRequestDTO;
import com.restdemo.mapper.RefreshTokenMapper;
import com.restdemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    RefreshTokenMapper refreshTokenMapper;

    @Autowired
    UserMapper userMapper;


    public RefreshToken createRefreshToken(String username){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userMapper.readUser(username));
        refreshToken.setToken(UUID.randomUUID().toString());
        // refreshToken.setExpiryDate(Instant.now().plusMillis(600000)); //10분
        refreshToken.setExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS)); // 30일
        refreshTokenMapper.createRefreshToken(refreshToken); // db에 refreshToken 정보 저장

        return refreshToken;
        // 클라이언트 측 쿠키 config와 서버 측 refreshToken 만료기간(30일) 일치시키기 (각자 설정해야함)
    }



    public RefreshToken findByToken(String token){
        return refreshTokenMapper.findByToken(token);
    }
    
    public RefreshToken findRefreshTokenByUsername(String username) {
    	return refreshTokenMapper.findRefreshTokenByUsername(username);
    }
    
    public void deleteRefreshToken(RefreshTokenRequestDTO refreshToken) {
    	refreshTokenMapper.deleteRefreshToken(refreshToken);
    }

    public void verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenMapper.deleteRefreshToken(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
    }
}
