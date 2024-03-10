### ------ Download dependencies and build ------
# Base image
FROM node:16-alpine AS build

# Set working directory
WORKDIR /app

# Copy package.json and package-lock.json
COPY ../project/package*.json ./

# Install dependencies
RUN npm install

# Copy app files
COPY ../project/ .

# Build Vue.js project
RUN npm run build

# Start the app
CMD ["npm", "run", "serve"]