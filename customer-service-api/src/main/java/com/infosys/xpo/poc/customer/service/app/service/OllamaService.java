package com.infosys.xpo.poc.customer.service.app.service;

import com.infosys.xpo.poc.customer.service.app.exception.CustomerServiceException;
import com.infosys.xpo.poc.customer.service.app.model.ChatMessage;
import com.infosys.xpo.poc.customer.service.app.model.ChatRequest;
import com.infosys.xpo.poc.customer.service.app.model.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OllamaService {

    @Value("${spring.ai.ollama.chat.options.model}")
    private String model;

    @Autowired
    private OllamaApi chatApi;

    public ChatResponse handleRequest(ChatRequest chatRequest) {
        log.info("ChatService::sendMessage START");
        try {
            OllamaApi.ChatRequest ollamaChatRequest = OllamaApi.ChatRequest.builder(model)
                    .withMessages(createMessageList(chatRequest))
                    .build();
            log.debug("Ollama Chat Request : {}", ollamaChatRequest);

            OllamaApi.ChatResponse ollamaChatResponse = chatApi.chat(ollamaChatRequest);

            log.debug("Ollama Chat Response : {}", ollamaChatResponse);

            String responseStr = ollamaChatResponse.message().content();
            log.debug("Response : {}", responseStr);

            return new ChatResponse(responseStr);
        } catch (Exception e) {
            log.error("Exception : {}", e.getMessage());
            throw new CustomerServiceException(e.getMessage(), e);
        } finally {
            log.info("ChatService::sendMessage END");
        }
    }

    private List<OllamaApi.Message> createMessageList(ChatRequest chatRequest) {

        // Add history of messages
        List<OllamaApi.Message> messages = chatRequest.getHistory().stream().map(message -> {
            OllamaApi.Message.Role role = "AI".equals(message.getFrom()) ? OllamaApi.Message.Role.ASSISTANT : OllamaApi.Message.Role.USER;
            return OllamaApi.Message.builder(role).withContent(message.getMessage()).build();
        }).collect(Collectors.toList());

        // Add current user message
        messages.add(OllamaApi.Message.builder(OllamaApi.Message.Role.USER).withContent(chatRequest.getMessage()).build());

        return messages;
    }
}