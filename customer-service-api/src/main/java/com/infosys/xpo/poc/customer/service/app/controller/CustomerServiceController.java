package com.infosys.xpo.poc.customer.service.app.controller;

import com.infosys.xpo.poc.customer.service.app.model.Request;
import com.infosys.xpo.poc.customer.service.app.model.Response;
import com.infosys.xpo.poc.customer.service.app.service.GenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CustomerServiceController {

    @Autowired
    private GenAiService genAiService;

    @PostMapping("/xpo/chat")
    public ResponseEntity<Response> handleRequest(@RequestBody Request request) {
        log.info("Request Received : {}", request);
        Response response = null;
        try {
            response = genAiService.handleRequest(request);
            return ResponseEntity.ok(response);
        } catch (Exception e ) {
          log.error("Error occurred while processing request : {}", e.getMessage());
          response = new Response("Error!!! Please try again!!!");
          return ResponseEntity.ok(response);
        } finally {
            log.info("Request processing Complete!!! Response : {}", response);
        }
    }
}
