package com.example.emos.wx.config.shiro;


import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${emos.jwt.secret}")
    private String secret;
    @Value("${emos.jwt.expire}")
    private int expire;

    public String createToken(int userId){
        //根据过期时间 密钥 Userid生成令牌字符串
        Date date=DateUtil.offset(new Date(), DateField.DAY_OF_YEAR,5);//偏移5天
        Algorithm algorithm= Algorithm.HMAC256(secret);//把密钥封装成加密算法对象
        JWTCreator.Builder builder= JWT.create();
        String token = builder.withClaim("userId", userId).withExpiresAt(date).sign(algorithm);
         return token;
    }
    public int getUserId(String token){//根据token获取userId
        DecodedJWT jwt = JWT.decode(token);
        Integer userId = jwt.getClaim("userId").asInt();//根据设置的token的名字获取userId，返回int
        return userId;
    }

    //验证
    public void verifierToken(String token){
        Algorithm algorithm=Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        verifier.verify(token);
    }
}
