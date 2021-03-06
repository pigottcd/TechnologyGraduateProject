package com.graduate.training.messaging;

import com.graduate.training.entities.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.Session;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.UUID;


@Component
public class ActiveMQSender {
    private static final Logger LOGGER = LogManager.getLogger(ActiveMQSender.class);
    private JmsTemplate jmsTemplate;
    @Autowired
    public ActiveMQSender(JmsTemplate template) {
        this.jmsTemplate = template;
    }
    public void send(Order message) {
        LOGGER.info("sending activemq message for order: " + message.getId());
        jmsTemplate.send("OrderBroker",
                (Session session)-> {
                    Message m = session.createTextMessage(message.toString());
                    m.setJMSCorrelationID(UUID.randomUUID().toString());
                    return m;
                });
    }
}
