package com.xyz.callbackservice.controller;

import com.xyz.callbackservice.domain.Registration;
import com.xyz.callbackservice.domain.RegistrationParameters;
import com.xyz.callbackservice.service.scheduling.CallbackSchedulingService;
import com.xyz.callbackservice.service.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path="/callback", produces="application/json")
public class CallbackController {

    @Autowired
    StorageService storageService;

    @Autowired
    CallbackSchedulingService callbackSchedulingService;

    @PostMapping(consumes="application/json")
    @ResponseBody
    public ResponseEntity<Registration> register(@Valid @RequestBody RegistrationParameters parameters) {
        Registration registration = storageService.create(parameters);

        callbackSchedulingService.scheduleTask(registration);

        return ResponseEntity.status(HttpStatus.CREATED).body(registration);
    }

    @DeleteMapping(path="/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deregister(@PathVariable("token") String token) {
        callbackSchedulingService.unscheduleTask(token);

        storageService.delete(token);
    }

    @PutMapping(path="/{token}", consumes="application/json")
    @ResponseBody
    public ResponseEntity<Registration> amend(@PathVariable("token") String token,
                                              @Valid @RequestBody RegistrationParameters parameters) {
        Registration registration = storageService.update(token, parameters);

        callbackSchedulingService.amendTask(registration);

        return ResponseEntity.status(HttpStatus.OK).body(registration);
    }
}

