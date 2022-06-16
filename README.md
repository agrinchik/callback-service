- This solution uses Spring/Spring Boot frameworks only
- Both the ThreadPoolTaskScheduler and ScheduledTaskRegistrar scheduling infrastructure were used to implement periodic task scheduling
- A number of concurrent requests to the REST API is limited by system resources as every request handled by a new thread in the web container
- Implemented unit tests for the RestController and StorageService classes
- No integration tests were implemented at the moment

1. To run the application on local environment:<br>
<b>./mvnw spring-boot:run</b>


2. Swagger UI is available to browse through REST API and for manual tests: <br>
   http://localhost:9000/swagger-ui/index.html