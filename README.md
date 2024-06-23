# Cyberry Enable - Task Management Service

Cyberry Enable is a system designed to create and manage processes in a simple, clear, and stable manner. It is a tool that allows users to easily design, expand, and manage both big and small processes without the need for installations, coding experience, hardware, or money. The main idea is to provide a simple flowchart tool for everyone to use and a way to execute these flowcharts as processes.

The application consists of two main parts: a process designer and a task runner. The processes are created using the designer and then executed by the process/task execution manager. The execution manager allows users to upload and execute processes, and then use a task manager to follow the workflow of tasks, completing tasks in real-time. This ensures efficiency and accuracy in following predefined processes and steps.

The tool can be used for business process automation, project management, and workflow optimisation. It supports collaboration, allowing teams to work on tasks simultaneously and track progress. The tool's simplicity and stability make it suitable for both small and large-scale processes. It can be used in various industries, including IT, finance, healthcare, and manufacturing. The tool's cloud-based nature ensures accessibility from any device.

## Services

The Cyberry Enable project consists of several services, each with its own responsibilities and functionalities. Here are the services:

- [Enable Gateway](https://github.com/cyberry-technologies/enable-gateway): This service is responsible for routing and load balancing between services.
- [Enable Registry](https://github.com/cyberry-technologies/enable-registry): This service is a service registry for service discovery and registration.
- [Enable Management Web Application](https://github.com/cyberry-technologies/enable-webapp-management): This is a web application for managing executions and tasks.
- [Enable API Auth Service](https://github.com/cyberry-technologies/enable-service-api-auth): Handles all functionality for authentication and user management.
- [Enable API Execution Service](https://github.com/cyberry-technologies/enable-service-api-execution): Handles all functionality for execution and task management.
- [Enable API Process Service](https://github.com/cyberry-technologies/enable-service-api-process): Handles all functionality for the publishing of processes and their versions.


## Instruction Guides
### Running Locally (Windows)
Use the docker-compose file to run the application locally. You can use the following commmand in your terminal (in the root folder) to run the entire project. This can take a few minutes the first time:
```cmd
docker-compose -f ./enable/docker/docker-compose.yml up -d 
```

Sometimes changes to the source code are not noticed by docker-compose. To manually run or rebuild a specific container, use the following command in terminal (in the root folder).
```cmd
docker-compose -f ./enable/docker/docker-compose.yml up -d --build <service-name>
```

### Infrastructure as Code
In the `infrastructure` folder, you can find the configuration for setting up a kubernetes cluster. Additionally, you can find scripts for handling these environments easier.

### Using Environment Setup and Delete tool (Windows)
You can set up and deploy the entire environment in Azure with one simple script as follows:

1. Make sure you have a Microsoft Azure account.
2. Make sure `azure-cli`, `jq` and `git` are installed on your system. If not, open Windows PowerShell as administrator and run the following command:
```PowerShell
choco install azure-cli jq git
```
3. Open Git Bash and move to the `/infrastructure/scripts` folder.
4. Simply run 
```git bash
./setup.sh
```
5. Follow the instructions from the setup tool and watch the tool and the GitHub pipeline handle the entire setup.

To delete the whole environment again, simply do the same for the `setup-delete.sh` file.
1. Make sure you are able to log into the Azure account related to the deployed application.
2. Make sure `azure-cli` and `jq` are installed on your system. If not, open Windows PowerShell as administrator and run the following command:
```PowerShell
choco install azure-cli jq
```
3. Open Git Bash and move to the `/infrastructure/scripts` folder.
4. Simply run 
```git bash
./setup-delete.sh
```
5. Follow the instructions from the setup tool and watch the tool handle the entire setup deletion.

### Adding/ Updating Secrets (Windows)
The system uses GitHub Secrets for security in terms of environment variables and credentials. For the deployment of production builds, environment variables using Kubernetes Secrets are set inside of the pipeline of this repository. This means there are 2 steps to adding or deleting secrets:

1. Add or remove a secret in the [main repository](https://github.com/cyberry-technologies/enable) of this project.
2. Add or remove a step in the GitHub pipeline `.github/workflows/main.yml` for creating and applying the specific secret.
3. Run the pipeline again by commiting and pushing changes.

To update a secret you do not need to change the pipeline:
1. Update a secret in the [main repository](https://github.com/cyberry-technologies/enable) of this project.
2. Run the pipeline again by commiting and pushing changes. If there are no changes to push, you can run the last pipeline run again.

### Running Tests (Windows)
There are 3 types of tests located in the `./test` folder of this repository: integration tests, end-to-end tests, load tests.

#### Integration tests (Postman)
The integration tests (located in `./test/integration-tests`) are meant for running in the pipeline. To run them your selfcompose the development application and import the collection and environment files into Postman.

#### End-to-end tests (Cypress)
The end-to-end tests (located in `./test/e2e-tests`) are meant for running in the pipeline but can also be run locally. To run them yourself compose the development application and see the Cypress documentation to follow the simple steps.

#### Load tests (K6)
The load tests are meant to be run on your local bc to test the deployed product. Before running these tests, make sure your application will not be used at the time by end users, since it is likely to have decreased performance during testing. To run the load tests, simply run one of the following options: low load, average load, high load or stress load. You can do this by running one of the following commands in the root of this repository:
```cmd
k6 run ./enable/test/load-tests/low-load-test.js
k6 run ./enable/test/load-tests/average-load-test.js
k6 run ./enable/test/load-tests/high-load-test.js
k6 run ./enable/test/load-tests/stress-load-test.js 
```

To save the output to a log file instead of print it in console. Use the following commands:
```cmd
k6 run ./enable/test/load-tests/low-load-test.js > output-low-load-test.log
k6 run ./enable/test/load-tests/average-load-test.js > output-average-load-test.log
k6 run ./enable/test/load-tests/high-load-test.js > output-low-high-test.log
k6 run ./enable/test/load-tests/stress-load-test.js > output-stress-load-test.log
```

### Applying Changes (Windows)
To adhere to best practices and security. I will only explain how to apply your changes using Git and the GitHub workflow and highly recommend using this approach.

#### 1. Install Git
- Download and install Git from [git-scm.com](https://git-scm.com/).
- Open your Windows Terminal and verify the installation with:
```cmd
git --version
```

#### 2. Commit Your Changes
- Navigate to the root of the repository directory and commit your changes:
```cmd
git add .
git commit -m "Your descriptive commit message"
```

#### 3. Merge Changes into main/master
- Ensure you are on the correct branch and merge your changes:
```cmd
git checkout main
git merge feature-branch-name
```

#### 4. Push Changes to Remote Repository
- Push your changes to GitHub:
```cmd
git push origin main
```
- Replace `main` with `master` if your main branch is named `master`.

These steps assume you have already configured Git with your GitHub account. For detailed instructions, refer to the GitHub documentation. Afterwards, you can see on the GitHub Actions page how the pipeline tries to apply possible changes in secrets and configuration files to the deployed system environment in Azure.