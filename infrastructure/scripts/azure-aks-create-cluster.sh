#!/bin/bash
az aks create --resource-group enableResourceGroupEastUS --name enable --node-count 2 --generate-ssh-keys
az aks get-credentials --resource-group enableResourceGroupEastUS --name enable