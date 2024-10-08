package com.restdemo.controller;

import com.restdemo.domain.*;
import com.restdemo.service.JwtService;
import com.restdemo.service.RefreshTokenService;
import com.restdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    JwtService jwtService;
    @Autowired
    RefreshTokenService refreshTokenService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;
    
    
    @PostMapping("/api/SignUp")
    public String SignUp(@RequestBody User user){
    	if(userService.readUser(user.getUsername()) == null) {
    		user.setPassword(passwordEncoder.encode(user.getPassword()));
    		userService.createUser(user);
    		return "회원가입을 축하드립니다.";
    	}
    	else {
    		return "이미 존재하는 아이디입니다.";
    	}
    }
    

    @PostMapping("/api/login")
    public JwtResponseDTO AuthenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
        if(authentication.isAuthenticated()){
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());
            JwtResponseDTO jwtResponseDTO = new JwtResponseDTO();
            jwtResponseDTO.setAccessToken(jwtService.GenerateToken(authRequestDTO.getUsername()));
            jwtResponseDTO.setToken(refreshToken.getToken());
            return jwtResponseDTO;
        } else {
            throw new UsernameNotFoundException("invalid user request..!!");
        }
    }

    @PostMapping("/api/refreshToken")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO){
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenRequestDTO.getToken());
        refreshTokenService.verifyExpiration(refreshToken);
        User user = refreshToken.getUser();
        if (user == null) {
            throw new RuntimeException("Refresh Token is not in DB..!!");
        }
        String accessToken = jwtService.GenerateToken(user.getUsername());

        JwtResponseDTO jwtResponseDTO = new JwtResponseDTO();
        jwtResponseDTO.setAccessToken(accessToken);
        jwtResponseDTO.setToken(refreshTokenRequestDTO.getToken());

        return jwtResponseDTO;

        /*return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    String accessToken = jwtService.GenerateToken(userInfo.getUsername());
                    return JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .token(refreshTokenRequestDTO.getToken()).build();
                }).orElseThrow(() ->new RuntimeException("Refresh Token is not in DB..!!"));*/
    }


}
