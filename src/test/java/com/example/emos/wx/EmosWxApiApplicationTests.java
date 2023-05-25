package com.example.emos.wx;

import cn.hutool.core.util.IdUtil;
import com.example.emos.wx.controller.form.leave_requestForm;
import com.example.emos.wx.db.dao.LeaveRequestDao;
import com.example.emos.wx.db.dao.MessageDao;
import com.example.emos.wx.db.dao.MessageRefDao;
import com.example.emos.wx.db.pojo.MessageEntity;
import com.example.emos.wx.db.pojo.MessageRefEntity;
import com.example.emos.wx.service.MessageService;
import com.example.emos.wx.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.mapping.TextScore;

import java.util.Date;
import java.util.HashMap;

@SpringBootTest
class EmosWxApiApplicationTests {
    @Autowired
   private MessageService messageService;

    @Autowired
    private MessageRefDao messageRefDao;

    @Autowired
    private MessageDao messageDao;


    @Autowired
    private UserService userService;

    @Autowired
    private LeaveRequestDao leaveRequestDao;

    @Test
    void contextLoads() {
        for (int i = 1; i <= 3; i++) {
            MessageEntity message = new MessageEntity();
            message.setUuid(IdUtil.simpleUUID());
            message.setSenderId(0);
            message.setSenderName("系统消息");
            message.setMsg("这是第" + i + "条测试消息");
            message.setSendTime(new Date());
            String id=messageService.insertMessage(message);

            MessageRefEntity ref=new MessageRefEntity();
            ref.setMessageId(id);
            ref.setReceiverId(40); //接收人ID
            ref.setLastFlag(true);
            ref.setReadFlag(false);
            messageService.insertRef(ref);
        }
    }


    @Test
    void test(){
        Date c=new Date();



        leave_requestForm Form=new leave_requestForm();
        Form.setEnddate(c);
        Form.setEnddate(new Date());
        Form.setLeavetype("dasdas");
        Form.setReason("dadasdas");
        Form.setEmployeeId(20);

        Date enddate = Form.getEnddate();


        userService.SubmitLeave(42,c,enddate,Form.getLeavetype(),Form.getReason());
    }



}
