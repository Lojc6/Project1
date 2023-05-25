package com.example.emos.wx;

import cn.hutool.core.util.IdUtil;
import com.example.emos.wx.db.dao.MessageDao;
import com.example.emos.wx.db.dao.MessageRefDao;
import com.example.emos.wx.db.pojo.MessageEntity;
import com.example.emos.wx.db.pojo.MessageRefEntity;
import com.example.emos.wx.service.MessageService;
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
        long l = messageRefDao.updateUnreadMessage("646dadb2e5297a1ed4f69783");
        System.out.println(l);
    }



}
