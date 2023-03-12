Blog application

Version 0.2.0

Updates

1. Spring Security, JWT authentication, JWT Filters for generating and validating JWT tokens. You can check the filters under 'filters' subfolder.
2. To implement security mechanics, a new entiy called User added into the project.
3. New endpoints for following functionalities
   1. The user must be able to view other users posts
   2. The user must be able to delete his posts
4. Swagger documentation for every endpoints
   1. localhost:xxxx/swagger-ui/index.html#/
5. Logging with SLF4J
6. Application is now using MySQL database. Migration applied with Flyway Migration Tool. You can check the migration scripts under resources/db/migrations subfolder
7. Application has a docker image. Link is below:
   1. https://hub.docker.com/r/tahatopal/blogapp
8. Testcontainers for integration testing

Application runs as a standart Spring Boot Application.
