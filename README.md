# cyberry-enable

## Running locally
Use the docker-compose file to run the application locally. You can use the following commmand in your terminal (in the root folder) to run the entire project. This can take a few minutes the first time:
```docker compose up -d```
Or:
```docker compose -f docker-compose.yml up -d```

Sometimes changes to the source code are not noticed by docker-compose. To manually run or rebuild a specific container, use the following command in terminal (in the root folder).
```docker compose up -d --build <service-name>```