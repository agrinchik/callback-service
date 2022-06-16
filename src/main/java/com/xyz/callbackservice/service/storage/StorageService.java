package com.xyz.callbackservice.service.storage;

import com.xyz.callbackservice.domain.Registration;
import com.xyz.callbackservice.domain.RegistrationParameters;

public interface StorageService {

	Registration create(RegistrationParameters parameters);

	Registration load(String token);

	Registration update(String token, RegistrationParameters parameters);

	void delete(String token);

}
