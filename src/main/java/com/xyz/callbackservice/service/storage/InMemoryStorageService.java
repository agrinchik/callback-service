package com.xyz.callbackservice.service.storage;

import com.xyz.callbackservice.domain.Registration;
import com.xyz.callbackservice.domain.RegistrationParameters;
import com.xyz.callbackservice.exception.StorageException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of in-memory data storage is done using Map<String, Registration> collection.
 * The following entries are stored (k -> v):
 * Token -> Registration
 */
@Service
public class InMemoryStorageService implements StorageService {

	private final Map<String, Registration> storage = new HashMap<>();

	@Override
	public synchronized Registration create(RegistrationParameters parameters) {
		if (containsUrl(parameters.getUrl())) {
			throw new StorageException("Cannot register a new URL as the URL already registered for callbacks");
		}

		Registration newRegistration = new Registration(parameters.getUrl(), parameters.getFrequency());
		storage.put(newRegistration.getToken(), newRegistration);
		return newRegistration;
	}

	@Override
	public synchronized Registration load(String token) {
		if (token == null || token.isEmpty()) {
			throw new StorageException("Registration token should be provided");
		}

		return storage.get(token);
	}

	@Override
	public synchronized Registration update(String token, RegistrationParameters parameters) {
		if (token == null || token.isEmpty()) {
			throw new StorageException("Registration token should be provided");
		}

		if (!storage.containsKey(token)) {
			throw new StorageException("Cannot update callback registration as provided token is invalid");
		}

		Registration registration = storage.get(token);

		if (!registration.getUrl().equals(parameters.getUrl())) {
			throw new StorageException("Cannot update callback registration as provided URL must be the same as originally registered");
		}

		registration.setFrequency(parameters.getFrequency());

		return registration;
	}

	@Override
	public synchronized void delete(String token) {
		if (token == null || token.isEmpty()) {
			throw new StorageException("Registration token should be provided");
		}

		if (!storage.containsKey(token)) {
			throw new StorageException("Cannot delete callback registration as provided token is invalid");
		}

		storage.remove(token);
	}

	public Map<String, Registration> getInternalStorage() {
		return storage;
	}

	private boolean containsUrl(String url) {
		return storage.values()
							.stream()
							.anyMatch(v -> v.getUrl().equals(url));
	}
}
