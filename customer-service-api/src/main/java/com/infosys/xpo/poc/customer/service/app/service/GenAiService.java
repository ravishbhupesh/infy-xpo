package com.infosys.xpo.poc.customer.service.app.service;

import com.infosys.xpo.poc.customer.service.app.model.Request;
import com.infosys.xpo.poc.customer.service.app.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GenAiService {

    public Response handleRequest(Request request) {

        return new Response(request.getMessage());
    }
}
