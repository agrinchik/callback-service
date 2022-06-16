package com.xyz.callbackservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import com.xyz.callbackservice.controller.CallbackController;
import com.xyz.callbackservice.domain.Registration;
import com.xyz.callbackservice.domain.RegistrationParameters;
import com.xyz.callbackservice.service.scheduling.CallbackSchedulingService;
import com.xyz.callbackservice.service.storage.StorageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.validation.Valid;

@ExtendWith(MockitoExtension.class)
public class CallbackControllerTests {

    String TEST_TOKEN = "a869bcde4535345";
    String TEST_URL = "http://TESTTEST.com";
    int TEST_FREQUENCY = 200;

    Registration TEST_REGISTRATION = new Registration(TEST_URL, TEST_FREQUENCY);

    @InjectMocks
    CallbackController callbackController;

    @Mock
    StorageService storageService;

    @Mock
    CallbackSchedulingService callbackSchedulingService;

    public ResponseEntity<Registration> register(@Valid @RequestBody RegistrationParameters parameters) {
        Registration registration = storageService.create(parameters);

        callbackSchedulingService.scheduleTask(registration);

        return ResponseEntity.status(HttpStatus.CREATED).body(registration);
    }

    @Test
    public void testRegisterCallback()
    {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(storageService.create(any(RegistrationParameters.class))).thenReturn(TEST_REGISTRATION);

        // when
        RegistrationParameters registrationParams = new RegistrationParameters();
        registrationParams.setUrl(TEST_URL);
        registrationParams.setFrequency(TEST_FREQUENCY);
        ResponseEntity<Registration> responseEntity = callbackController.register(registrationParams);

        // then
        Assertions.assertEquals(responseEntity.getStatusCodeValue(), 201);
        Assertions.assertEquals(responseEntity.getBody(), TEST_REGISTRATION);
        verify(storageService).create(registrationParams);
        verify(callbackSchedulingService).scheduleTask(TEST_REGISTRATION);
    }

    @Test
    public void testDeregisterCallback()
    {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // when
        callbackController.deregister(TEST_TOKEN);

        // then
        verify(callbackSchedulingService).unscheduleTask(TEST_TOKEN);
        verify(storageService).delete(TEST_TOKEN);
    }

    @Test
    public void testAmendCallback()
    {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(storageService.update(eq(TEST_TOKEN), any(RegistrationParameters.class))).thenReturn(TEST_REGISTRATION);

        // when
        RegistrationParameters registrationParams = new RegistrationParameters();
        registrationParams.setUrl(TEST_URL);
        registrationParams.setFrequency(TEST_FREQUENCY);
        ResponseEntity<Registration> responseEntity = callbackController.amend(TEST_TOKEN, registrationParams);

        // then
        Assertions.assertEquals(responseEntity.getStatusCodeValue(), 200);
        Assertions.assertEquals(responseEntity.getBody(), TEST_REGISTRATION);
        verify(storageService).update(TEST_TOKEN, registrationParams);
        verify(callbackSchedulingService).amendTask(TEST_REGISTRATION);
    }
}
