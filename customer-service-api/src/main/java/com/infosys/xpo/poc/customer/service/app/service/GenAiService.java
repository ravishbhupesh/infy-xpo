package com.infosys.xpo.poc.customer.service.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.xpo.poc.customer.service.app.exception.CustomerServiceException;
import com.infosys.xpo.poc.customer.service.app.model.ChatRequest;
import com.infosys.xpo.poc.customer.service.app.model.ChatResponse;
import com.infosys.xpo.poc.customer.service.app.model.genai.Message;
import com.infosys.xpo.poc.customer.service.app.model.genai.Request;
import com.infosys.xpo.poc.customer.service.app.model.genai.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GenAiService {

    @Value("${app.openai.model}")
    private String openaiModel;
    @Value("${app.openai.apiKey}")
    private String apiKey;

    @Value("${app.openai.url}")
    private String openaiUrl;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    private final HttpHeaders headers;

    public GenAiService() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
    }

    public ChatResponse handleRequest(ChatRequest chatRequest) {
        log.info("ChatService::sendMessage START");
        try {
            Request request = createRequest(List.of(chatRequest.getMessage()));
            String requestBody = objectMapper.writeValueAsString(request);
            log.debug("Request created for openAi : {}", requestBody);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            String responseRaw = restTemplate.exchange(openaiUrl, HttpMethod.POST, entity, String.class).getBody();
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
        chatResponse.setMessage((null != response) ? response.getChoices().get(0).getMessage().getContent() : "null");
        return chatResponse;
    }

    private Request createRequest(List<String> message) {
        Request request = new Request();
        request.setModel(openaiModel);
        request.setMessages(message.stream().map(m -> new Message("user", m)).collect(Collectors.toList()));
        return request;
    }
}
