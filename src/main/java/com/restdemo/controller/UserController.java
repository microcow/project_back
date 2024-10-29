package com.restdemo.controller;

import com.restdemo.domain.*;
import com.restdemo.service.JwtService;
import com.restdemo.service.RefreshTokenService;
import com.restdemo.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
    
    @PostMapping("/api/IdCheck")
    public String IdCheck(@RequestBody User user){
    	if(userService.readUser(user.getUsername()) == null) {
    		return "사용가능 한 아이디입니다.";
    	}
    	else {
    		return "이미 존재하는 아이디입니다.";
    	}
    }
    
    @PostMapping("/api/CheckAuthByCookie")
    public Collection<GrantedAuthority> CheckAuthByCookie(@AuthenticationPrincipal UserDetails userDetails){
    	String username = userDetails.getUsername();
    	Collection<GrantedAuthority> userAuth = userService.getAuthorities(username);
    	return userAuth;
    }
    
    
    @PostMapping("/api/SignUp")
    public String SignUp(@RequestBody User user){ /// ResponseEntity<String>로 return하도록 수정하기!! (updateUser 참고)
    	if(userService.readUser(user.getUsername()) == null) {
    		user.setPassword(passwordEncoder.encode(user.getPassword()));
    		userService.createUser(user);
    		return "회원가입을 축하드립니다.";
    	}
    	else {
    		return "이미 존재하는 아이디입니다.";
    	}
    }
    
    @PostMapping("/api/SignIn")
    public JwtResponseDTO Signin(@RequestBody User user){ 
    	User getUser = userService.readUser(user.getUsername());
    	if (getUser.getUsername().equals(user.getUsername()) ||
    			passwordEncoder.encode(getUser.getPassword()).equals(passwordEncoder.encode(user.getPassword()))) {
    		
    		AuthRequestDTO token = new AuthRequestDTO();
    		token.setUsername(user.getUsername());
    		token.setPassword(user.getPassword()); // authenticationManager메서드는 내부적으로 사용자가 입력한 비밀번호와 DB에 저장된 암호화된 비밀번호를 비교할 때 passwordEncoder.matches() 메서드를 사용합니다. 이 과정에서 원본 비밀번호를 그대로 사용해야 올바르게 검증됩니다.
    		
    		return AuthenticateAndGetToken(token);// token 생성 과정에서 시큐리티의 authenticationManager메서드가 호출됨
    	}
    	else return null;
    }

    @PostMapping("/api/SetToken")
    public JwtResponseDTO AuthenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
        if(authentication.isAuthenticated()){
        	
            // 액세스 토큰(jwt) 발급
            JwtResponseDTO jwtResponseDTO = new JwtResponseDTO();
            jwtResponseDTO.setAccessToken(jwtService.GenerateToken(authRequestDTO.getUsername()));  
            
            // 리프레쉬 토큰(refreshToken) 발급 (*DB에 해당 username으로 유효한 리프레쉬 토큰이 없는 경우에만 발급)
            // * 리프레쉬 토큰만 DB에 저장됨
            if(refreshTokenService.findRefreshTokenByUsername(authRequestDTO.getUsername()) == null) {
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());
                jwtResponseDTO.setToken(refreshToken.getToken());
            }
            return jwtResponseDTO; // 액세스 토큰과 리프레쉬 토큰 두개를 return (리프레쉬 토큰이 발급된 경우)
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
    }
    
    @PostMapping("/api/DeleterefreshToken")
    public String DeleterefreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO){
    	 if (refreshTokenRequestDTO.getToken() == null || refreshTokenRequestDTO.getToken().isEmpty()) {
    		 return "refreshToken is Empty!";
    	 }
    	 else refreshTokenService.deleteRefreshToken(refreshTokenRequestDTO);
    	      return "complete!";
    }
    
    @PostMapping("/api/readUserList")
    public List<User> readUserList(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO){
    	List<User> userList = new ArrayList();
    	userList = userService.readUserList();
    	return userList;
    }
    
    @PostMapping("/api/admin/readUserByUsername")
    public User readUserByUsername(@RequestHeader("Authorization") String jwt,
    							   @RequestBody User user ){
    	User edituser = userService.readUser(user.getUsername());
    	
    	return edituser; 
    }
    
    @PostMapping("/api/admin/deleteUser") /// ResponseEntity<String>로 return하도록 수정하기!! (updateUser 참고)
    public String deleteUser(@RequestHeader("Authorization") String jwt,
    						 @RequestBody User user ){
    	String username = user.getUsername();
    	if(username == null) {
    		return "정보없음";
    	}
    	else
    		userService.deleteUser(username);
    	return "삭제완료"; 
    }
    
    @PostMapping("/api/admin/updateUser") /// 회원 검색 기능 만들기
    public ResponseEntity<String> updateUser(@RequestHeader("Authorization") String jwt,
    						 @RequestBody User user) {
    	
    	if(user.getIndex() == null || user.getIndex().equals("null")) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("회원정보를 불러오지 못했습니다.");
    	}
    	
    	try {
    	user.sanitizeFields(); // 문자열 "null"을 전달받았을 경우 값을 null로 변경하는 메서드 (userUpdate xml에서 값이 null일 경우 정보 업데이트를 진행하지 않기 위함)
    	userService.updateUser(user);
    	return ResponseEntity.ok("회원정보가 수정되었습니다.");
    	}catch (Exception e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원정보 수정 중 오류가 발생했습니다.");
    	}
    }
}
