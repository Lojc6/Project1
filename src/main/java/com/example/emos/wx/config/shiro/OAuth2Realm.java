package com.example.emos.wx.config.shiro;

import com.example.emos.wx.db.pojo.TbUser;
import com.example.emos.wx.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component //记得
public class OAuth2Realm extends AuthorizingRealm {

    @Autowired
    private JwtUtil jwtUtil;//因为后续要用到JwtUtil来认证令牌,所以把工具类导入

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token; //判断传入的令牌是不是我们自己定义的OAuth2Token类
    }
    /*
    * 授权（验证权限时调用)
    * */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection collection) {//授权方法
        TbUser user =(TbUser) collection.getPrimaryPrincipal();
        int userId=user.getId();
        // 查询用户的权限列表
        Set<String> permsSet = userService.searchUserPermissions(userId);
        SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
        // 把权限列表添加到Info对象中
        info.setStringPermissions(permsSet);

        return info;
    }
    /*
    * 认证验证登录时调用）
    * */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken Token) throws AuthenticationException {//认证方法
        String access=(String)Token.getPrincipal();
        int userId=jwtUtil.getUserId(access);
        TbUser user=userService.searchById(userId);


        if (user==null){
            throw new LockedAccountException("账户已被锁定,请联系管理员");
        }
        SimpleAuthenticationInfo info=new SimpleAuthenticationInfo(user,access,getName());//get当前类名字

        //TODO 往info对象中添加用户信息、Token字符串
        return info;
    }
}
