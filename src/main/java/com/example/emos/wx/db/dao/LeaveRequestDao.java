package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.LeaveRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

@Mapper
public interface LeaveRequestDao {
    public int insert(HashMap param);
}