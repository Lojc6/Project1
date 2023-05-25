package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.LeaveRequest;

public interface LeaveRequestDao {
    int deleteByPrimaryKey(Integer id);

    int insert(LeaveRequest record);

    int insertSelective(LeaveRequest record);

    LeaveRequest selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LeaveRequest record);

    int updateByPrimaryKey(LeaveRequest record);
}