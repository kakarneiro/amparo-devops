#!/usr/bin/env bash
# Provisiona a VM no Azure via Azure CLI.
# Pre-requisito: az login (ou o Azure Cloud Shell, que ja vem logado).
# Uso: bash scripts/azure-vm.sh
#
# Nota: na assinatura Azure for Students a serie B (Standard_B2s) nao estava
# disponivel em Brazil South nem em East US (SkuNotAvailable), por isso usamos
# Standard_D2as_v4 em East US. O IP publico e criado antes da VM para evitar
# o erro "publicIPAddresses not found" do template padrao.
set -euo pipefail

RESOURCE_GROUP="rg-amparo"
LOCATION="eastus"
VM_NAME="vm-amparo"
VM_SIZE="Standard_D2as_v4"       # 2 vCPU / 8 GB
IMAGE="Ubuntu2204"
ADMIN_USER="azureuser"
PUBLIC_IP_NAME="vm-amparo-ip"

echo "==> 1/5 Criando grupo de recursos $RESOURCE_GROUP em $LOCATION"
az group create --name "$RESOURCE_GROUP" --location "$LOCATION" --output table

echo "==> 2/5 Criando o IP publico estatico $PUBLIC_IP_NAME"
az network public-ip create --resource-group "$RESOURCE_GROUP" --name "$PUBLIC_IP_NAME" \
  --sku Standard --allocation-method Static --output table

echo "==> 3/5 Criando a VM $VM_NAME ($IMAGE, $VM_SIZE)"
az vm create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$VM_NAME" \
  --image "$IMAGE" \
  --size "$VM_SIZE" \
  --admin-username "$ADMIN_USER" \
  --generate-ssh-keys \
  --public-ip-address "$PUBLIC_IP_NAME" \
  --output table

echo "==> 4/5 Liberando a porta 8080 (API) no NSG"
az vm open-port --resource-group "$RESOURCE_GROUP" --name "$VM_NAME" --port 8080 --priority 1010 --output table

echo "==> 5/5 IP publico da VM:"
PUBLIC_IP=$(az network public-ip show --resource-group "$RESOURCE_GROUP" --name "$PUBLIC_IP_NAME" --query ipAddress -o tsv)
echo "$PUBLIC_IP"
echo
echo "Conecte com:  ssh $ADMIN_USER@$PUBLIC_IP"
echo "Depois rode na VM:"
echo "  curl -fsSL https://raw.githubusercontent.com/kakarneiro/amparo-devops/main/scripts/setup-vm.sh | bash"
