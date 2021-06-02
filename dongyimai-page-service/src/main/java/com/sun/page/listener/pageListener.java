package com.sun.page.listener;

import com.sun.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class pageListener implements MessageListener {
   @Autowired
   private PageService pageService;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String text = textMessage.getText();
            System.out.println("接收到消息:"+text);
            boolean b = pageService.genItemHtml(Long.parseLong(text));

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
