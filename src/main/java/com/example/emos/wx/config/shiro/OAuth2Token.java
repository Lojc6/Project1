package com.example.emos.wx.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;

public class OAuth2Token implements AuthenticationToken {

    private String token;

    public OAuth2Token(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }//获取身份

    @Override
    public Object getCredentials() {
        return token;
    }//获取凭证
}
