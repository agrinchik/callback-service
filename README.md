- This solution uses Spring/Spring Boot frameworks only
- Both the ThreadPoolTaskScheduler and ScheduledTaskRegistrar scheduling infrastructure were used to implement periodic task scheduling
- A number of concurrent requests to the REST API is limited by system resources as every request handled by a new thread in the web container
- Implemented unit tests for the RestController and StorageService classes
- No integration tests were implemented at the moment

<u><b>REST API:</b></u>

<b>POST /callback</b> <br>
Client can register a new URL for callback with specified callback frequency. The API returns registration token which can be subsequently used to amend or delete the registration. 

<b>PUT /callback/{token} </b> <br>
Client can amend currently registered URL by providing the token, url and the new frequency.

<b>DELETE /callback/{token} </b> <br>
Client can de-register previously registered URL by providing the previously returned token.

1. To run the application on local environment:

./mvnw spring-boot:run


2. Swagger UI is available to browse through REST API and for manual tests: <br>
   http://localhost:9000/swagger-ui/index.html