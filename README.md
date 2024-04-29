# Cyberry Enable - Task Management Service

Cyberry Enable is a system designed to create and manage processes in a simple, clear, and stable manner. It is a tool that allows users to easily design, expand, and manage both big and small processes without the need for installations, coding experience, hardware, or money. The main idea is to provide a simple flowchart tool for everyone to use and a way to execute these flowcharts as processes.

The application consists of two main parts: a process designer and a task runner. The processes are created using the designer and then executed by the process/task execution manager. The execution manager allows users to upload and execute processes, and then use a task manager to follow the workflow of tasks, completing tasks in real-time. This ensures efficiency and accuracy in following predefined processes and steps.

The tool can be used for business process automation, project management, and workflow optimisation. It supports collaboration, allowing teams to work on tasks simultaneously and track progress. The tool's simplicity and stability make it suitable for both small and large-scale processes. It can be used in various industries, including IT, finance, healthcare, and manufacturing. The tool's cloud-based nature ensures accessibility from any device.

## Services

The Cyberry Enable project consists of several services, each with its own responsibilities and functionalities. Here are the services:

1. [Enable Gateway](https://github.com/cyberry-technologies/enable-gateway): This service is responsible for routing and load balancing between services.

2. [Enable Registry](https://github.com/cyberry-technologies/enable-registry): This service is a service registry for service discovery and registration.

3. [Enable Management Web Application](https://github.com/cyberry-technologies/enable-webapp-management): This is a web application for managing executions and tasks.

4. [Enable Engine Service](https://github.com/cyberry-technologies/enable-service-engine): This service handles all intensive task management functionality.

5. [Enable Execution API Service](https://github.com/cyberry-technologies/enable-service-execution): This service provides execution and task management capabilities.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

The following tools are required to run this project:

1. Docker
2. Kubernetes (kubectl)
3. Postman
4. SonarQube
5. Vue.js (for the web application)

Please refer to the individual service READMEs for specific installation instructions.

### Running the Project

To run the whole project, use the following command:

```bash
docker-compose -f ./docker/docker-compose.yml up
```

To stop running the project:

```bash
docker-compose -f ./docker/docker-compose.yml down
```

## Integration with Other Services

Each service within the Cyberry Enable project has its own access points, dependencies, and required external information. Please refer to the individual service READMEs for specific details.

## Stakeholders

The main stakeholders for this project are:

1. Example stakeholder (SH00)
2. Non-technical entrepreneur(s) in small or concept startup(s) (SH01)

## Repository Link

You can find the repository for the Cyberry Enable - Task Management Service project [here](https://github.com/cyberry-technologies/enable).

## Running locally
Use the docker-compose file to run the application locally. You can use the following commmand in your terminal (in the root folder) to run the entire project. This can take a few minutes the first time:
```docker compose up -d```
Or:
```docker compose -f docker-compose.yml up -d```

Sometimes changes to the source code are not noticed by docker-compose. To manually run or rebuild a specific container, use the following command in terminal (in the root folder).
```docker compose up -d --build <service-name>```