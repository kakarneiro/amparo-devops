#!/usr/bin/env bash
# Roda DENTRO da VM (Ubuntu 22.04): instala Docker + Compose, clona o repositorio e sobe os containers.
# Uso: bash setup-vm.sh
set -euo pipefail

REPO_URL="https://github.com/kakarneiro/amparo-devops.git"
APP_DIR="$HOME/amparo-devops"

echo "==> 1/5 Instalando Docker Engine e Docker Compose plugin"
sudo apt-get update -y
sudo apt-get install -y ca-certificates curl gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" \
  | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update -y
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

echo "==> 2/5 Permitindo usar docker sem sudo"
sudo usermod -aG docker "$USER"

echo "==> 3/5 Clonando o repositorio"
if [ -d "$APP_DIR" ]; then
  cd "$APP_DIR" && git pull
else
  git clone "$REPO_URL" "$APP_DIR" && cd "$APP_DIR"
fi

echo "==> 4/5 Criando .env a partir do exemplo (se nao existir)"
[ -f .env ] || cp .env.example .env

echo "==> 5/5 Subindo os containers em background"
sudo docker compose up -d --build

echo
echo "Pronto. Confira:"
echo "  sudo docker compose ps"
echo "  sudo docker compose logs -f api"
echo "  curl http://localhost:8080/actuator/health"
echo "Saia e entre de novo no SSH para usar docker sem sudo."
