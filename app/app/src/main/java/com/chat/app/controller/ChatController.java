package com.chat.app.controller;

import com.chat.app.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles all incoming messages being sent from the frontend that is being sent to the backend.
 * Broadcast it to all the clients.
 */

@Controller
public class ChatController {
    /**
     * Handles messages. When a message is brodcasted over "/app/sendMessage"
     * it will send to all other connected clients onto this particular topic
     * @param message
     * @return a message that is sent over the broadcast
     */
    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message){
        return message;
    }

    @GetMapping("chat")
    public String chat() {
        return "chat";
    }
}
