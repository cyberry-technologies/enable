# enable-execution-service

The `enable-execution-service` is a microservice that handles everything to do with storing general process objects, managing those processes, and storing information and details about those them. It is part of a larger application and is designed to be used by other applications that require process management functionality.

## Getting Started

### Prerequisites

To run this service, you'll need to have the following tools and technologies installed:

* Java 17
* Maven
* Spring Boot
* MySQL
* Hibernate
* IntelliJ (or any other IDE of your choice)

### Installing

1. Clone this repository to your local machine.

```git clone https://github.com/cyberry-technologies/cyberry-enable-design-process-service.git```

2. Use docker compose in the `cyberry-enable` folder to run entire application
3. Use ```docker compose up -d --build cyberry-enable-design-process-service``` to build the specific container

### Endpoints

The `cyberry-enable-process-service` provides the following endpoints:

* `GET /processes`: Retrieves all processes owned by the authenticated user.
* `POST /processes`: Creates a new process with the given name and returns the created process.
* `GET /processes/{id}`: Retrieves the details of the process with the given ID.

### Database

The `cyberry-enable-process-service` uses a MySQL database to store process data. The following entities are defined:

* `Process`: Represents a process and has the following fields:
  * `id`: The ID of the process.
  * `ownerUserFK`: The ID of the user that owns the process.
  * `name`: The name of the process.
  * `createdAt`: The timestamp when the process was created.
* `ProcessDetails`: Represents the details of a process and has the following fields:
  * `description`: A description of the process.
  * `inputTypeDescription`: A description of the input data required by the process.
  * `outputTypeDescription`: A description of the output data produced by the process.
