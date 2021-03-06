package com.vomvos.communicator.controllers;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import com.vomvos.communicator.models.ChatGroup;
import com.vomvos.communicator.models.ChatMessage;
import com.vomvos.communicator.models.user.User;
import com.vomvos.communicator.services.user.UserService;


@Controller
public class ChatController {
	
	public static final Logger logger= LoggerFactory.getLogger(ChatController.class);
	
	@Autowired
	private SimpMessageSendingOperations messagingTemplate;
	
    @MessageMapping("/chat.sendMessage")
    
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
    	logger.info(chatMessage.getSender()+" "+chatMessage.getMessage());
    	messagingTemplate.convertAndSend("/topic/public",chatMessage);
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, 
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
    	logger.info(chatMessage.getSender()+" is getting added");
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
    
    @MessageMapping("/chat.chatGroup")
    public void sendToUser(@Payload ChatMessage chatMessage,SimpMessageHeaderAccessor headerAccessor){
    	String reciever = chatMessage.getReceiver();
    	messagingTemplate.convertAndSendToUser(chatMessage.getSender(),"/reply",chatMessage);
    	messagingTemplate.convertAndSendToUser(reciever,"/reply",chatMessage);
    	logger.info(reciever);
    	
    	
    }

}
