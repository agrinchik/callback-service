package com.xyz.callbackservice.service.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class RestService {

    @Autowired
    RestTemplate restTemplate;

    public Object sendPost(String url, Object body, Class<?> clazz) {
        log.info("Sending POST request to {}, body: {}", url, body);
        return restTemplate.postForObject(url, body, clazz);
    }

}
