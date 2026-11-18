#!/usr/bin/env bash
# Provisiona a VM no Azure via Azure CLI.
# Pre-requisito: az login (ou o Azure Cloud Shell, que ja vem logado).
# Uso: bash scripts/azure-vm.sh
set -euo pipefail

RESOURCE_GROUP="rg-amparo"
LOCATION="brazilsouth"
VM_NAME="vm-amparo"
VM_SIZE="Standard_B2s"            # 2 vCPU / 4 GB: suficiente para API + Postgres
IMAGE="Ubuntu2204"
ADMIN_USER="azureuser"

echo "==> 1/4 Criando grupo de recursos $RESOURCE_GROUP em $LOCATION"
az group create --name "$RESOURCE_GROUP" --location "$LOCATION" --output table

echo "==> 2/4 Criando a VM $VM_NAME ($IMAGE, $VM_SIZE)"
az vm create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$VM_NAME" \
  --image "$IMAGE" \
  --size "$VM_SIZE" \
  --admin-username "$ADMIN_USER" \
  --generate-ssh-keys \
  --public-ip-sku Standard \
  --output table

echo "==> 3/4 Liberando a porta 8080 (API) no NSG"
az vm open-port --resource-group "$RESOURCE_GROUP" --name "$VM_NAME" --port 8080 --priority 1010 --output table

echo "==> 4/4 IP publico da VM:"
PUBLIC_IP=$(az vm show -d --resource-group "$RESOURCE_GROUP" --name "$VM_NAME" --query publicIps -o tsv)
echo "$PUBLIC_IP"
echo
echo "Conecte com:  ssh $ADMIN_USER@$PUBLIC_IP"
echo "Depois rode:  bash setup-vm.sh   (copie o script para a VM com scp ou cole o conteudo)"
