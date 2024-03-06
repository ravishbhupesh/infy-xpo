package com.infosys.xpo.poc.customer.service.app.controller;

import com.infosys.xpo.poc.customer.service.app.model.ChatRequest;
import com.infosys.xpo.poc.customer.service.app.model.ChatResponse;
import com.infosys.xpo.poc.customer.service.app.service.GenAiService;
import com.infosys.xpo.poc.customer.service.app.service.OllamaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CustomerServiceController {

    @Autowired
    private GenAiService genAiService;

    @Autowired
    private OllamaService ollamaService;

    @PostMapping("/xpo/chat")
    public ResponseEntity<ChatResponse> handleRequest(@RequestBody ChatRequest chatRequest) {
        log.info("Request Received : {}", chatRequest);
        ChatResponse chatResponse = null;
        try {
            chatResponse = genAiService.handleRequest(chatRequest);
            //chatResponse = ollamaService.handleRequest(chatRequest);
            return ResponseEntity.ok(chatResponse);
        } catch (Exception e ) {
          log.error("Error occurred while processing request : {}", e.getMessage());
          chatResponse = new ChatResponse("Error!!! Please try again!!!");
          return ResponseEntity.ok(chatResponse);
        } finally {
            log.info("Request processing Complete!!! Response : {}", chatResponse);
        }
    }
}
