package com.infosys.xpo.poc.customer.service.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.xpo.poc.customer.service.app.exception.CustomerServiceException;
import com.infosys.xpo.poc.customer.service.app.model.ChatMessage;
import com.infosys.xpo.poc.customer.service.app.model.ChatRequest;
import com.infosys.xpo.poc.customer.service.app.model.ChatResponse;
import com.infosys.xpo.poc.customer.service.app.model.genai.Message;
import com.infosys.xpo.poc.customer.service.app.model.ollama.Request;
import com.infosys.xpo.poc.customer.service.app.model.ollama.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class OllamaService {

    @Value("${app.ollama.model}")
    private String model;
    @Value("${app.ollama.apiKey}")
    private String apiKey;

    @Value("${app.ollama.url}")
    private String url;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    private final HttpHeaders headers;

    public OllamaService() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.set("Authorization", "Bearer " + apiKey);
    }

    public ChatResponse handleRequest(ChatRequest chatRequest) {
        log.info("ChatService::sendMessage START");
        try {
            Request request = createRequest(chatRequest);
            String requestBody = objectMapper.writeValueAsString(request);
            log.debug("Request created for ollama : {}", requestBody);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            String responseRaw = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
            log.debug("open ai raw response : {}", responseRaw);

            Response response = objectMapper.readValue(responseRaw, Response.class);
            log.debug("Object response : {}", response);

            return createResponse(response);
        } catch (Exception e) {
            log.error("Exception : {}", e.getMessage());
            throw new CustomerServiceException(e.getMessage(), e);
        } finally {
            log.info("ChatService::sendMessage END");
        }
    }

    private ChatResponse createResponse(Response response) {
        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setMessage((null != response) ? response.getMessage().getContent() : "null");
        return chatResponse;
    }

    private Request createRequest(ChatRequest chatRequest) {
        Request request = new Request();
        request.setModel(model);
        List<Message> messages = new LinkedList<>();
        List<ChatMessage> history = chatRequest.getHistory();
        int i = 0;
        while (i < history.size()) {
            ChatMessage chatMessage = history.get(i);
            messages.add(i++, new Message("AI".equals(chatMessage.getFrom()) ? "assistant" : "user" , chatMessage.getMessage()));
        } ;
        messages.add(i, new Message("User", chatRequest.getMessage()));
        request.setMessages(messages);
        request.setStream(false);
        return request;
    }
}
