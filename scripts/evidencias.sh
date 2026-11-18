#!/usr/bin/env bash
# Gera as evidencias pedidas na entrega de DevOps, em sequencia. Rode na VM, na pasta do projeto.
# Uso: bash scripts/evidencias.sh > docs/evidencias.txt 2>&1
set -u
API=http://localhost:8080

echo "===== 1. CONTAINERS EM EXECUCAO ====="; docker compose ps
echo; echo "===== 2. LOGS DA API (ultimas 30 linhas) ====="; docker compose logs --tail=30 api
echo; echo "===== 3. LOGS DO BANCO (ultimas 15 linhas) ====="; docker compose logs --tail=15 db
echo; echo "===== 4. USUARIO DENTRO DO CONTAINER DA API (nao-root) ====="; docker compose exec api whoami
echo; echo "===== 5. WORKDIR E ESTRUTURA DE DIRETORIOS ====="; docker compose exec api sh -c 'pwd && ls -la'
echo; echo "===== 6. VARIAVEIS DE AMBIENTE DO CONTAINER ====="; docker compose exec api sh -c 'env | grep -E "DB_|SERVER_PORT|SPRING_PROFILES" '
echo; echo "===== 7. VOLUME NOMEADO ====="; docker volume inspect amparo-db-data
echo; echo "===== 8. HEALTH ====="; curl -s $API/actuator/health; echo

echo; echo "===== 9. CRUD: LOGIN ====="
TOKEN=$(curl -s -X POST $API/auth/login -H 'Content-Type: application/json' -d '{"email":"admin@amparo.com","senha":"admin123"}' | sed -E 's/.*"token":"([^"]+)".*/\1/')
echo "token obtido: ${TOKEN:0:30}..."

echo; echo "===== 10. CRUD: CREATE (POST /cidadaos) ====="
curl -s -X POST $API/cidadaos -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"nome":"Antonio Ferreira","dataNascimento":"1945-11-02","telefone":"11999990003","endereco":"Rua Verde, 55","condicao":"Idoso"}'; echo
echo; echo "===== 11. CRUD: READ (GET /cidadaos) ====="; curl -s "$API/cidadaos?size=5" -H "Authorization: Bearer $TOKEN"; echo
echo; echo "===== 12. CRUD: UPDATE (PUT /cidadaos/3) ====="
curl -s -X PUT $API/cidadaos/3 -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"nome":"Antonio Ferreira da Silva","dataNascimento":"1945-11-02","telefone":"11999990003","endereco":"Rua Verde, 55","condicao":"Idoso","ativo":true}'; echo
echo; echo "===== 13. EVENTO DO ESP32 (POST /eventos) ====="
curl -s -X POST $API/eventos -H 'Content-Type: application/json' -H 'X-Device-Token: esp32-amparo-dev-token' \
  -d '{"codigoSerial":"ESP32-0001","tipo":"BOTAO_EMERGENCIA","descricao":"Evidencia DevOps"}'; echo
echo; echo "===== 14. CRUD: DELETE (DELETE /cidadaos/3) ====="; curl -s -o /dev/null -w "HTTP %{http_code}\n" -X DELETE $API/cidadaos/3 -H "Authorization: Bearer $TOKEN"

echo; echo "===== 15. SELECT DIRETO NO BANCO (persistencia) ====="
docker compose exec -T db psql -U amparo -d amparo -c '\dt'
docker compose exec -T db psql -U amparo -d amparo -c 'SELECT id, nome, condicao FROM tb_cidadao;'
docker compose exec -T db psql -U amparo -d amparo -c 'SELECT e.id, e.tipo, e.status, d.codigo_serial FROM tb_evento e JOIN tb_dispositivo d ON d.id = e.dispositivo_id;'
docker compose exec -T db psql -U amparo -d amparo -c 'SELECT a.id, c.nome AS cuidador, a.resultado FROM tb_atendimento a JOIN tb_cuidador c ON c.id = a.cuidador_id;'
