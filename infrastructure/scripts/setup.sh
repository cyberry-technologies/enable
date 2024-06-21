#!/bin/bash
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

#---------- Login to Azure ---------- 
# echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} ${YELLOW}Logging in to Azure...${NC}"
# login_output=$(az login)
# tenantId=$(echo $login_output | jq -r '.[0].tenantId')
# echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} ${GREEN}Logged in to Azure...${NC}"

# echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} Your Azure subscriptions: "
# az account list --output table

# echo -e "\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} Enter your preferred tenantId: "
# read tenantId

# echo "tenantId=$tenantId" > preferences.env
# echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} Stored preference for tenantId: $tenantId"

# echo -e "\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} Enter your preferred subscriptionId: "
# read subscriptionId

# az account set --subscription $subscriptionId
# echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} Set subscription to $subscriptionId"

# echo "subscriptionId=$subscriptionId" >> preferences.env
# echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} Stored preference for subscriptionId: $subscriptionId"

# # #---------- Create resource group ----------
# echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} ${YELLOW}Creating resource group...${NC}"
# az group create --name enableAKSResourceGroupEastUS --location eastus
# echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} ${GREEN}Created resource group enableAKSResourceGroupEastUS.${NC}"


# # #---------- Create RBAC for cluster ----------
# echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} ${YELLOW}Creating service principal with Contributor role for AKS cluster...${NC}"
# sp=$(az ad sp create-for-rbac --name enableAKSClusterServicePrincipal --query "{appId:appId, password:password}" -o tsv)
# read -r appId clientSecret <<< "$sp"
# az role assignment create --assignee $appId --role Contributor --scope subscriptions/$subscriptionId/resourceGroups/enableAKSResourceGroupEastUS
# echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} ${GREEN}Created service principal with Contributor role for AKS cluster.${NC}"

# echo "appId=$appId" >> preferences.env
# echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} Stored preference for service principal appId: $appId"

# #---------- Create Cluster ----------
# echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} ${YELLOW}Creating AKS cluster...${NC}"
# az aks create --resource-group enableAKSResourceGroupEastUS --name enable --service-principal $appId --client-secret $clientSecret --node-count 2 --generate-ssh-keys
# echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} ${GREEN}Created AKS cluster enable.${NC}"


# #---------- Set kubectl context ----------
# az aks get-credentials --resource-group enableAKSResourceGroupEastUS --name enable
# echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} Set kubectl context to AKS cluster enable"


#---------- Securely handle preferred secret values ----------
echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} Before continuing, make sure you have entere the (preffered) values for the following secret(s) in the "enable" Github repository:"
echo -e ""
echo -e "${PURPLE}[Cyberry Enable Setup Tool]:${NC}     AZURE_CREDENTIALS:
{
  \"clientId\": \""$appId"\",
  \"clientSecret\": "$clientSecret"\",
  \"tenantId\": \""$tenantId"\",
  \"subscriptionId\": \""$subscriptionId"\"
}"
echo -e "${PURPLE}[Cyberry Enable Setup Tool]:${NC}     DB_AUTH_CONNECTION_STRING: The connection string to database for the authentication service."
echo -e "${PURPLE}[Cyberry Enable Setup Tool]:${NC}     DB_EXECUTION_CONNECTION_STRING: The connection string to database for the execution service."
echo -e "${PURPLE}[Cyberry Enable Setup Tool]:${NC}     DB_PROCESS_CONNECTION_STRING: The connection string to database for the process service."
echo -e "${PURPLE}[Cyberry Enable Setup Tool]:${NC}     GATEWAY_KEY_ALIAS: The key alias of your SSL certficate."
echo -e "${PURPLE}[Cyberry Enable Setup Tool]:${NC}     GATEWAY_KEY_STORE_PASSWORD: The key store password of your SSL certficate."
echo -e "${PURPLE}[Cyberry Enable Setup Tool]:${NC}     RABBITMQ_USERNAME: The username you want to use for RabbitMQ."
echo -e "${PURPLE}[Cyberry Enable Setup Tool]:${NC}     RABBITMQ_PASSWORD: The password you want to use for RabbitMQ."
echo -e "${PURPLE}[Cyberry Enable Setup Tool]:${NC}     SERVICE_AUTH_JWT_KEY: The key you want to use for JWT token."
echo -e "${PURPLE}[Cyberry Enable Setup Tool]:${NC}     SERVICE_AUTH_JWT_ISSUER: The issuer you want to use for JWT token."
echo -e "${PURPLE}[Cyberry Enable Setup Tool]:${NC}     SERVICE_AUTH_JWT_AUDIENCE: The audience you want to use for JWT token."
echo -e "${PURPLE}[Cyberry Enable Setup Tool]:${NC}     SERVICE_ENCRYPTION_KEY: The key for the services for encrypting data in rest. Must be 32 characters long."
echo -e ""
echo -e "${PURPLE}[Cyberry Enable Setup Tool]:${NC} Press enter when completed:"
read -r

#---------- Commit and push change to main branch to deploy ----------
echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} ${YELLOW}Trying to commit and push change to master branch to deploy...${NC}"
git add .
git commit -m "Setup AKS cluster"
git push origin master 
echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} ${GREEN}Done committing and pushing change to master branch to deploy.${NC}"


echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} ${GREEN}DONE... Setup of Azure environment completed successfully${NC}"
