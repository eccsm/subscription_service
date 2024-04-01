# Newsletter

Welcome to the Newsletter! This Java Spring Boot application handles user subscriptions to your newsletter through a RESTful API. Below is an overview of the project, instructions for running and testing the service, and Swagger UI integration for API documentation.

## Project Structure

The application is structured to manage user subscriptions and newsletter distribution. Key components include:

- **SubscriptionController:** Handles subscription-related endpoints.
- **SubscriptionService:** Manages subscription-related business logic.
- **SubscriptionRepository:** Interface for subscription data access.
- **User:** Represents a user entity.

## Getting Started

### Prerequisites

Ensure you have the following installed:

- Java 8 or later
- Maven

### Installation

1. Download the Source Code: Download the source code of the newsletter subscription service directly from cloud.

2. Navigate to the Project Directory: Extract the downloaded source code if it's in a compressed format.

3. Open a terminal/command prompt. Navigate to the root directory of the extracted source code.
`cd path/to/newsletter`
This command will clean the project, compile the source code, run tests, and package the application.

4. Run the Application: After a successful build, you can run the application using Maven:`mvn spring-boot:run`

    Alternatively, you can find the generated JAR file in the target directory and run it using:
`java -jar target/newsletter-0.0.1-SNAPSHOTjar`

    Ensure that any required dependencies are available, and the application should start.

### Database Configuration

The application utilizes an in-memory database for easy testing. Database configurations can be adjusted in the `application.properties` file.

## Running the Application

1. Run the Spring Boot application: `mvn spring-boot:run`
2. Open your browser and go to [http://localhost:8080](http://localhost:8080)

## API Specification with Swagger UI

Explore and interact with the API using Swagger UI. After running the application, open your browser and go to [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) for an interactive API documentation.

Swagger UI provides a user-friendly interface to:

- Subscribe a user
- Unsubscribe a user
- Check subscription status
- List subscriptions by date
- List all subscriptions before and/or after a given date

### Subscribe User

Endpoint: `POST /subscribe`

Request:
```json
{
  "userId": "exampleUserId"
}
```

### Unsubscribe User
Endpoint: POST /unsubscribe

Request:
```json
{
  "userId": "exampleUserId"
}
```

### Check Subscription Status
Endpoint: GET /subscription/{userId}

Response:
```json
{
  "subscribed": true
}
```

For detailed API documentation please go to [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Docker Integration
Create a Docker image: docker build -t newsletter .
Run the Docker container: docker run -p 8080:8080 newsletter

### Testing
Run unit tests using: `mvn test`

### Contact
For any inquiries, feel free to [contact me](mailto:ekincan@casim.net).

