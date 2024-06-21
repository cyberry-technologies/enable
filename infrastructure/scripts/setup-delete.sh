#!/bin/bash
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

#---------- Login to Azure ---------- 
source preferences.env
echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} Stored preference for tenantId: $tenantId"
echo -e "\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} Stored preference for subscriptionId: $subscriptionId"
echo -e "\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} Stored preference for appId: $appId"

echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} ${YELLOW}Loging in to Azure...${NC}"
login_output=$(az login)
tenantId=$(echo $login_output | jq -r '.[0].tenantId')
echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} ${GREEN}Logged in to Azure...${NC}"

echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} Your Azure subscriptions: "
az account list --output table

echo -e "\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} Enter your preferred tenantId: "
read tenantId

echo "tenantId=$tenantId" > preferences.env
echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} Stored preference for tenantId: $tenantId"

echo -e "\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} Enter your preferred subscriptionId: "
read subscriptionId

az account set --subscription $subscriptionId
echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} Set subscription to $subscriptionId"

echo "subscriptionId=$subscriptionId" >> preferences.env
echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} Stored preference for subscriptionId: $subscriptionId"

az aks get-credentials --resource-group enableAKSResourceGroupEastUS --name enable
echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} Set kubectl context to AKS cluster enable"

# #---------- Delete Cluster ----------
echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} ${YELLOW}Deleting AKS cluster...${NC}"
az aks delete --resource-group enableAKSResourceGroupEastUS --name enable --yes --no-wait
echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} ${GREEN}Deleted AKS cluster enable${NC}"

# #---------- Delete RBAC for cluster ----------
echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} ${YELLOW}Deleting service principal for AKS cluster...${NC}"
az ad sp delete --id $appId
echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} ${GREEN}Deleted service principal for AKS cluster${NC}"

# #---------- Delete resource group ----------
echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} ${YELLOW}Deleting resource group...${NC}"
az group delete --name enableAKSResourceGroupEastUS --yes --no-wait
echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} ${GREEN}Deleted resource group enableAKSResourceGroupEastUS${NC}"


echo -e "\n\n${PURPLE}[Cyberry Enable Setup Tool]:${NC} ${GREEN}DONE... Deletion of Azure setup completed successfully${NC}"

