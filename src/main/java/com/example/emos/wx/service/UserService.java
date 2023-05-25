package com.example.emos.wx.service;

import com.example.emos.wx.db.pojo.TbUser;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public interface UserService {
    public int register(String registerCode,String code,String nickname,String photo);

    public Set<String> searchUserPermissions(int userId);

    public Integer login(String code);

    public TbUser searchById(int userId);

    public String searchUserHiredate(int userId);

    public HashMap searchUserSummary(int userId);

    public int SubmitLeave(int id, Date creat,Date end,String leaveType,String reason);
}
