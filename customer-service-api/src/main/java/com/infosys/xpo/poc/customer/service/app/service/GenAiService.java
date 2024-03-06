package com.infosys.xpo.poc.customer.service.app.service;

import com.infosys.xpo.poc.customer.service.app.exception.CustomerServiceException;
import com.infosys.xpo.poc.customer.service.app.model.ChatRequest;
import com.infosys.xpo.poc.customer.service.app.model.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GenAiService {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;
    @Value("${spring.ai.openai.chat.model}")
    private String model;
    @Value("${spring.ai.openai.chat.temperature}")
    private Float temperature;

    public ChatResponse handleRequest(ChatRequest chatRequest) {
        log.info("ChatService::sendMessage START");
        try {
            log.info("API Key : {}", apiKey);
            OpenAiApi openAiApi = new OpenAiApi(apiKey);

            OpenAiApi.ChatCompletionRequest chatCompletionRequest = new OpenAiApi.ChatCompletionRequest(createMessageList(chatRequest), model, temperature);
            log.debug("Chat Completion Request : {}", chatCompletionRequest);

            ResponseEntity<OpenAiApi.ChatCompletion> chatCompletionResponseEntity = openAiApi.chatCompletionEntity(chatCompletionRequest);

            OpenAiApi.ChatCompletion chatCompletion = chatCompletionResponseEntity.getBody();

            String responseStr = null;
            if (null != chatCompletion) {
                List<OpenAiApi.ChatCompletion.Choice> choices = chatCompletion.choices();
                OpenAiApi.ChatCompletionMessage chatCompletionMessage = choices.get(0).message();
                responseStr = chatCompletionMessage.content();
            }

            return new ChatResponse(responseStr);
        } catch (Exception e) {
            log.error("Exception : {}", e.getMessage());
            throw new CustomerServiceException(e.getMessage(), e);
        } finally {
            log.info("ChatService::sendMessage END");
        }
    }

    private List<OpenAiApi.ChatCompletionMessage> createMessageList(ChatRequest chatRequest) {

        // Add history of messages
        List<OpenAiApi.ChatCompletionMessage> messages = chatRequest.getHistory().stream().map(m -> {
            OpenAiApi.ChatCompletionMessage.Role role = "AI".equals(m.getFrom()) ? OpenAiApi.ChatCompletionMessage.Role.ASSISTANT : OpenAiApi.ChatCompletionMessage.Role.USER;
            return new OpenAiApi.ChatCompletionMessage(m.getMessage(), role);
        }).collect(Collectors.toList());

        messages.add(new OpenAiApi.ChatCompletionMessage(chatRequest.getMessage(), OpenAiApi.ChatCompletionMessage.Role.USER));

        return messages;
    }

}
