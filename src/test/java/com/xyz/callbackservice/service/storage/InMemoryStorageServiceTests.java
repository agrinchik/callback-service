package com.xyz.callbackservice.storage;

import com.xyz.callbackservice.domain.Registration;
import com.xyz.callbackservice.domain.RegistrationParameters;
import com.xyz.callbackservice.service.storage.InMemoryStorageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

public class InMemoryStorageServiceTests {

    String TEST_URL = "http://testurl.com";
    int TEST_FREQUENCY = 5;
    int TEST_FREQUENCY_MILLIS = TEST_FREQUENCY * 60 * 1000;

    InMemoryStorageService storageService;

    @Mock Map<String, Registration> storage;

    @BeforeEach
    public void setup() {
        storageService = new InMemoryStorageService();
    }

    @Test
    void givenRegistrationParams_whenCreate_thenSuccessfullyStoresNewEntry() {
        // Given
        RegistrationParameters regParameters = new RegistrationParameters();
        regParameters.setUrl(TEST_URL);
        regParameters.setFrequency(TEST_FREQUENCY);

        // When
        Registration registration = storageService.create(regParameters);

        // Then
        Assertions.assertNotNull(registration);
        Assertions.assertNotNull(registration.getToken());
        Assertions.assertNotNull(registration.getUrl());
        Assertions.assertFalse(registration.getToken().isEmpty());
        Assertions.assertEquals(registration.getToken().length(), 32);
        Assertions.assertEquals(registration.getUrl(), TEST_URL);
        Assertions.assertEquals(registration.getFrequency(), TEST_FREQUENCY);
        Assertions.assertEquals(registration.getFrequencyMillis(), TEST_FREQUENCY_MILLIS);
    }

    @Test
    void givenUrlAlreadyRegistered_whenCreate_thenThrowsStorageException() {
        // Given
        RegistrationParameters regParameters = new RegistrationParameters();
        regParameters.setUrl(TEST_URL);
        regParameters.setFrequency(TEST_FREQUENCY);
        storageService.create(regParameters);

        // When
        try {
            storageService.create(regParameters);
            fail("Will give an error");
        } catch(Exception ex) {
            Assertions.assertEquals(ex.getMessage(), "Cannot register a new URL as the URL already registered for callbacks");
        }
    }

    // Other unit tests are typical and will not be implemented in this test solution
}