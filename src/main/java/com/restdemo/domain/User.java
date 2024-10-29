package com.restdemo.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class User implements UserDetails {
    private static final long serialVersionUID = 1L;

    private String index;
    private String username; // ID
    private String password; // password
    private String name; // 이름
    private String regisDateTime; // 가입일
    private String updateDateTime; // 정보 수정일
    private String email; // 이메일
    private String address; //주소
    private String number; // 연락처
    private String auth; // 권한
    private String point; // 포인트

    //security 관련
    private Collection<? extends GrantedAuthority> authorities;

    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;
    

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegisDateTime() {
		return regisDateTime;
	}

	public void setRegisDateTime(String regisDateTime) {
		this.regisDateTime = regisDateTime;
	}

	public String getUpdateDateTime() {
		return updateDateTime;
	}

	public void setUpdateDateTime(String updateDateTime) {
		this.updateDateTime = updateDateTime;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNumber() {
		return number;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAccountNonExpired(boolean isAccountNonExpired) {
        this.isAccountNonExpired = isAccountNonExpired;
    }

    public void setAccountNonLocked(boolean isAccountNonLocked) {
        this.isAccountNonLocked = isAccountNonLocked;
    }

    public void setCredentialsNonExpired(boolean isCredentialsNonExpired) {
        this.isCredentialsNonExpired = isCredentialsNonExpired;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        return authorities;
    }

    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        return password;
    }

    @Override
    public String getUsername() {
        // TODO Auto-generated method stub
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return isEnabled;
    }
    
    // 변수 값이 문자열 "null"일 경우 null 값으로 값을 정제하는 메서드
    public void sanitizeFields() {
    	if ("null".equals(this.username)) {
            this.username = null;
        }
    	if ("null".equals(this.name)) {
            this.name = null;
        }
        if ("null".equals(this.number)) {
            this.number = null;
        }
        if ("null".equals(this.address)) {
            this.address = null;
        }
        if ("null".equals(this.email)) {
            this.email = null;
        }
        if ("null".equals(this.auth)) {
            this.auth = null;
        }
        if ("null".equals(this.point)) {
            this.point = null;
        }
    }

}

