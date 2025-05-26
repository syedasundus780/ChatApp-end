package com.goldencat.chatapp.controller;

import com.goldencat.chatapp.model.Message;
import com.goldencat.chatapp.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat")
    public void process(@Payload Message message) {
        try {
            // Set timestamp if not already set
            if (message.getTimestamp() == null) {
                message.setTimestamp(new Date());
            }
            
            // Save the message and get the saved instance
            Message savedMessage = messageService.save(message);
            
            // Send the saved message to receiver
            messagingTemplate.convertAndSendToUser(
                    savedMessage.getReceiverId(),
                    "/queue/messages",
                    savedMessage
            );
        } catch (Exception e) {
            // Send error message back to sender
            messagingTemplate.convertAndSendToUser(
                    message.getSenderId(),
                    "/queue/errors",
                    "Failed to send message: " + e.getMessage()
            );
        }
    }

    @GetMapping("/messages/{senderId}/{receiverId}")
    public ResponseEntity<List<Message>> getChatMessages(
            @PathVariable String senderId,
            @PathVariable String receiverId
    ) {
        var chatMessages = messageService.getChatMessage(senderId, receiverId);
        return ResponseEntity.ok(chatMessages);
    }
}
