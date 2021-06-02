package com.sun.sms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.*;

@RestController
public class smsController {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination queueSmsDestination;

    @RequestMapping("/sendsms")
    public String sendMsg(final String mobile,final String msg){
        jmsTemplate.send(queueSmsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("mobile",mobile);
                mapMessage.setString("param",msg);
                return mapMessage;
            }
        });
        return "sendOK!!!!";
    }
}
