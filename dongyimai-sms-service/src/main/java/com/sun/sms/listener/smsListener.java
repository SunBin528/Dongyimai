package com.sun.sms.listener;

import com.sun.sms.service.SmsUtil;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
@Component
public class smsListener implements MessageListener {

    @Autowired
    private SmsUtil smsUtil;
    @Override
    public void onMessage(Message message) {
        if(message instanceof MapMessage){
            MapMessage mapMessage = (MapMessage) message;

            try {
                System.out.println("收到短信发送请求---mobile:"
                        +mapMessage.getString("mobile")+"param:"+mapMessage.getString("param"));
               HttpResponse response = smsUtil.sendMessage(mapMessage.getString("mobile"), mapMessage.getString("param"));

                System.out.println("请求结束:"+response.getEntity());
            } catch (JMSException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
