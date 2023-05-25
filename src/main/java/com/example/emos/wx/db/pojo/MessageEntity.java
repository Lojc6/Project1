package com.example.emos.wx.db.pojo;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

//继承Serilizable的原因
/*
 最重要的两个原因是：

　　1、将对象的状态保存在存储媒体中以便可以在以后重新创建出完全相同的副本；

　　2、按值将对象从一个应用程序域发送至另一个应用程序域。

    实现serializable接口的作用是就是可以把对象存到字节流，然后可以恢复。所以你想如果你的对象没实现序列化怎么才能进行网络传输呢，要网络传输就得转为字节流，所以在分布式应用中，你就得实现序列化，如果你不需要分布式应用，那就没那个必要实现序列化。

   分布式应用（distributedapplication）：指的是应用程序分布在不同计算机上，通过网络来共同完成一项任务的工作方式。

  */
@Data
@Document(collection = "message")
public class MessageEntity implements Serializable {

    @Id
    private String _id;

    @Indexed(unique = true)
    private String uuid;

    @Indexed
    private Integer senderId;


    private String senderPhoto="https://lojc719-1318318357.cos.ap-guangzhou.myqcloud.com/img/System.jpg";

    private String senderName;

    private String msg;

    @Indexed
    private Date sendTime;
}
