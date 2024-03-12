# Cyberry Auth User Service

Cyberry Auth User Service is a microservice that takes care of all user functionality, including authentication, authorization, and user information management.

## Prerequisites

- Java 17
- Maven
- Spring boot 3.0.5
- MySQL
- Hibernate

## Getting Started

To get started with this service, follow these steps:

1. Clone the repository
2. Use docker compose in the `cyberry-enable` folder to run entire application
3. Use ```docker compose up -d --build cyberry-auth-user-service``` to build the specific container

## Endpoints

The following endpoints are available:

- `/api/auth/signup` - Creates a new user account
- `/api/auth/signin` - Authenticates a user and generates a JWT token
- `/api/auth/checkUsernameAvailability?username={username}` - Checks if the specified username is available
- `/api/auth/checkEmailAvailability?email={email}` - Checks if the specified email address is available
- `/api/user/me` - Retrieves the current user's information

## Contributors

- Liam Vaessen (@liamvaessen)

## License

This project is licensed under the MIT License - see the `LICENSE` file for details.

