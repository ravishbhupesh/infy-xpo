package com.infosys.xpo.poc.customer.service.app.model.ollama;

import com.infosys.xpo.poc.customer.service.app.model.genai.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    private String model;
    private List<Message> messages;
    private boolean stream;
}