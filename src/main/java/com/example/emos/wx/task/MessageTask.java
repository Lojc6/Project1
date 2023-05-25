package com.example.emos.wx.task;


import com.example.emos.wx.db.pojo.MessageEntity;
import com.example.emos.wx.db.pojo.MessageRefEntity;
import com.example.emos.wx.exception.EmosException;
import com.example.emos.wx.service.MessageService;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class MessageTask {

    @Autowired
    private ConnectionFactory factory;

    @Autowired
    private MessageService messageService;

    public void send(String topic, MessageEntity entity){
          String id=messageService.insertMessage(entity);
          try(//try语句会自动关闭连接和通道
              Connection connection= factory.newConnection();
              Channel channel=connection.createChannel();

          ){
              channel.queueDeclare(topic,true,false,false,null);//队列名称、是否持久化存储、允许其它消费者也连接到topic，false==加锁、是否自动删除、参数null没啥参数
              HashMap map=new HashMap();
              map.put("messageId",id);
              AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().headers(map).build();
              channel.basicPublish("",topic,properties,entity.getMsg().getBytes(StandardCharsets.UTF_8));
              log.debug("消息发送成功");
          }catch (Exception e){
             log.error("执行异常",e);
             throw new EmosException("向MQ发送消息失败");
          }
    }

    @Async
    public void sendAsync(String topic, MessageEntity entity){
       send(topic,entity);
    }

    public int receive(String topic){
        int i=0;
        try(//try语句会自动关闭连接和通道
            Connection connection= factory.newConnection();
            Channel channel=connection.createChannel();     )
        {
            channel.queueDeclare(topic,true,false,false,null);//队列名称、是否持久化存储、允许其它消费者也连接到topic，false==加锁、是否自动删除、参数null没啥参数
            while(true){
                GetResponse response=channel.basicGet(topic,false);//第二个参数是不要自动ack应答。
                if (response != null) {
                    AMQP.BasicProperties props = response.getProps();
                    Map<String, Object> map = props.getHeaders();
                    String messageId = map.get("messageId").toString();
                    byte[] body = response.getBody();
                    String message=new String(body);
                    log.debug("从RabbitMQ接收的消息:"+message);

                    MessageRefEntity entity=new MessageRefEntity();
                    entity.setMessageId(messageId);
                    entity.setReceiverId(Integer.parseInt(topic));
                    entity.setReadFlag(false);
                    entity.setLastFlag(true);
                    messageService.insertRef(entity);

                    long deliveryTag = response.getEnvelope().getDeliveryTag();
                    channel.basicAck(deliveryTag,false);
                    i++;
                }
                else {
                    break;
                }
            }
        }catch (Exception e){
            log.error("执行异常",e);
            throw new EmosException("接收消息失败");
        }
        return i;
    }

    @Async
    public int receiveAsync(String topic){
       return  receive(topic);
    }

    public void deleteQueue(String topic){
        try(//try语句会自动关闭连接和通道
            Connection connection= factory.newConnection();
            Channel channel=connection.createChannel();     )
        {
         channel.queueDelete(topic);
         log.error("消息队列删除成功");

        }catch (Exception e){
            log.error("删除队列失败",e);
            throw new EmosException("删除队列失败");
        }
    }

    @Async
    public void deleteQueueAsync(String topic){
        deleteQueue(topic);
    }

}
