package com.example.emos.wx.service.impl;


import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emos.wx.db.dao.TbUserDao;
import com.example.emos.wx.db.pojo.MessageEntity;
import com.example.emos.wx.db.pojo.TbUser;
import com.example.emos.wx.exception.EmosException;
import com.example.emos.wx.service.UserService;
import com.example.emos.wx.task.MessageTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

@Service
@Slf4j
@Scope("prototype")
public class UserServiceImpl implements UserService {

    @Value("${wx.app-id}")
    private String appId;

    @Value("${wx.app-secret}")
    private String appSecret;

    @Autowired
    private TbUserDao userDao;

    @Autowired
    private MessageTask messageTask;

    private String getOpenId(String code){
        String url="https://api.weixin.qq.com/sns/jscode2session";
        HashMap map=new HashMap<>();
        map.put("appid",appId);
        map.put("secret",appSecret);
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String resp = HttpUtil.post(url, map);
        JSONObject jsonObject = JSONUtil.parseObj(resp);
        String openid = jsonObject.getStr("openid");
        if (openid==null||openid.length()==0){
            throw new RuntimeException("临时登录凭证错误");
        }
        return openid;
    }

    @Override
    public int register(String registerCode, String code, String nickname, String photo) {
       if (registerCode.equals("646458")){
           boolean bool = userDao.haveRootUser();
           if (!bool){//可以成为管理员
               String openId=getOpenId(code);
               HashMap param=new HashMap();
               param.put("openId", openId);
               param.put("nickname", nickname);
               param.put("photo", photo);
               param.put("role", "[0]");
               param.put("status", 1);
               param.put("createTime", new Date());
               param.put("root", true);
               param.put("hiredate",new Date());
               userDao.insert(param);
               int id=userDao.searchIdByOpenId(openId);

               MessageEntity entity=new MessageEntity();
               entity.setSenderId(0);
               entity.setSenderName("系统消息");
               entity.setUuid(IdUtil.simpleUUID());
               entity.setMsg("欢迎您注册成为超级管理员,请及时更新您的员工个人信息");
               entity.setSendTime(new Date());
               messageTask.sendAsync(id+"",entity);

               return id;
           }else{
               throw new EmosException("无法成为超级管理员");
           }
       }
       else {

       }
       return 0;
    }

    @Override
    public Set<String> searchUserPermissions(int userId) {
        Set<String> permissions = userDao.searchUserPermissions(userId);
        return permissions;
    }

    @Override
    public Integer login(String code) {
        String openId=getOpenId(code);
        Integer id = userDao.searchIdByOpenId(openId);
        if (id==null){
            throw new EmosException("账户不存在");
        }
        //TODO 从消息队列中接收消息,转移到消息表
        /*messageTask.receiveAsync(id+"");*/
        return id;
    }

    @Override
    public TbUser searchById(int userId) {
        TbUser tbUser = userDao.searchById(userId);
        return tbUser;
    }

    @Override
    public String searchUserHiredate(int userId) {
        String hiredate=userDao.searchUserHiredate(userId);
        return hiredate;
    }

    @Override
    public HashMap searchUserSummary(int userId) {
        HashMap map=userDao.searchUserSummary(userId);
        return map;

    }
}
