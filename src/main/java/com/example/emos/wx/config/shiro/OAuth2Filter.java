package com.example.emos.wx.config.shiro;


import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.apache.http.HttpStatus;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Scope("prototype")//要用到多例,不然ThreadLocal读取数据会出问题,默认是singleton
public class OAuth2Filter extends AuthenticatingFilter {
    @Autowired
    private ThreadLocalToken threadLocalToken;

    @Value("${emos.jwt.cache-expire}")
    private int cacheExpire;

    @Autowired
    private JwtUtil jwtUtil;//需要调用JWTUTIL里面的方法校验

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req= (HttpServletRequest) request;
        String token=getRequestToken(req);
        if (StrUtil.isBlank(token)){
            return null;
        }
        return new OAuth2Token(token);//把令牌字符串封装为令牌对象返回，这个令牌对象是我们自己定义过得,这个类继承了AuthenticationToken
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        //判断哪个方法被shiro框架处理
        HttpServletRequest req= (HttpServletRequest) request;
        if (req.getMethod().equals(RequestMethod.OPTIONS.name())){
            return true;
        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req=(HttpServletRequest) request;
        HttpServletResponse resp=(HttpServletResponse) response;
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        //设置跨域请求参数
        resp.setHeader("Access-Control-Allow-Credentials","true");
        resp.setHeader("Access-Control-Allow-Origin",req.getHeader("Origin"));

        //清空数据
        threadLocalToken.clear();

        String token=getRequestToken(req);
        if (StrUtil.isBlank(token)){
              resp.setStatus(HttpStatus.SC_UNAUTHORIZED);
              resp.getWriter().print("无效令牌");
              return false;
        }
        try {
            jwtUtil.verifierToken(token);
        }catch (TokenExpiredException e){//令牌过期
          if (redisTemplate.hasKey(token)){
              //客户端的令牌过期,redis还存着则要更新令牌
              redisTemplate.delete(token);
              int userId = jwtUtil.getUserId(token);
              token=jwtUtil.createToken(userId);
              redisTemplate.opsForValue().set(token,userId+"",cacheExpire, TimeUnit.DAYS);
                                           // key?     value   这个是上面定义的过期int   单位
              threadLocalToken.setToken(token);
          }else{
              resp.setStatus(HttpStatus.SC_UNAUTHORIZED);
              resp.getWriter().print("令牌已过期");
              return false;
          }
        }catch(Exception e){
            resp.setStatus(HttpStatus.SC_UNAUTHORIZED);
            resp.getWriter().print("无效令牌");
            return false;
        }
       boolean bool= executeLogin(request,response);//通过这个方法让shiro间接调用Realm类
        return bool;
    }

    @Override//认证！！！失败的时候触发这个函数
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletRequest req=(HttpServletRequest) request;
        HttpServletResponse resp=(HttpServletResponse) response;
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        //设置跨域请求参数
        resp.setHeader("Access-Control-Allow-Credentials","true");
        resp.setHeader("Access-Control-Allow-Origin",req.getHeader("Origin"));
        resp.setStatus(HttpStatus.SC_UNAUTHORIZED);
        try {
            resp.getWriter().print(e.getMessage());
        } catch (Exception exception) {

        }
        return false;
    }

    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        super.doFilterInternal(request, response, chain);
    }

    private String getRequestToken(HttpServletRequest request){
        String token=request.getHeader("token");//获取请求头的token
        if (StrUtil.isBlank(token)){
            token=request.getParameter("token");
        }
        return token;
    }
}
