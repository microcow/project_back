package com.restdemo.mapper;

import com.restdemo.domain.RefreshToken;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefreshTokenMapper {
    public RefreshToken findByToken(String token);

    public void createRefreshToken(RefreshToken refreshToken);

    public void deleteRefreshToken(RefreshToken refreshToken);
}
