package com.xyz.callbackservice.domain;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class RegistrationParameters {

    @NotNull
    @NotBlank(message="URL is required")
    private String url;

    @Min(value = 1, message = "Callback frequency should not be less than 1 minutes")
    @Max(value = 240, message = "Callback frequency should not be greater than 240 minutes")
    private int frequency;
}
