package com.xyz.callbackservice.domain;

import lombok.Data;

import java.util.UUID;

@Data
public class Registration {

    private String token;

    private String url;

    /**
     * Frequency value stored in minutes
     */
    private int frequency;

    public Registration(String url, int frequency) {
        this.token = generateToken();
        this.url = url;
        this.frequency = frequency;
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-","");
    }

    public int getFrequencyMillis() {
        return this.frequency * 60 * 1000;
    }

}

