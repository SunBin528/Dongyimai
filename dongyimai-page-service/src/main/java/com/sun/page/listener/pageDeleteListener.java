package com.sun.page.listener;

import com.sun.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

public class pageDeleteListener implements MessageListener {
    @Autowired
    private PageService pageService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Long[] goodsId=(Long[]) objectMessage.getObject();
            System.out.println("监听接收到消息..."+goodsId);
            boolean b = pageService.deleteItemHtml(goodsId);
            System.out.println("删除页面结果"+b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
