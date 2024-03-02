package com.infosys.xpo.poc.customer.service.app.model.ollama;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.infosys.xpo.poc.customer.service.app.model.genai.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

    private String model;
    private Message message;
}
