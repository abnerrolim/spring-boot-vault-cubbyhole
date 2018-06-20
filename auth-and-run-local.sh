#!/bin/bash
WLL_JAR="target/spring-boot-vault-cubbyhole.jar"
if [ $# -eq 0 ]
  then
    echo "No arguments supplied. Using default"
  else
  	WLL_JAR=$1
fi

function exportVars {
    echo "Exporting vault vars..."
    export VAULT_TOKEN="87e7784b-d598-44fe-8962-c7c345a11eed"
    export VAULT_ADDR="http://0.0.0.0:8200"
}

exportVars

mkdir -p resources
cd resources/

echo "mounting app backend"

./vault mount  -path=app  generic

echo "writing app policy..."
./vault policy-write app-policy app-policy.txt

response=$(./vault auth tune -max-lease-ttl=2m token/)
echo tune token response: $response

echo "Generating token..."
token_response=$(curl -v -H "Accept: application/json" -H "Content-type: application/json" -H "X-Vault-Token: $VAULT_TOKEN" -H "X-Vault-Wrap-TTL: 5m" -X POST -d '{"policies":["app-policy"],"ttl":"2m","renewable":true}'  http://0.0.0.0:8200/v1/auth/token/create)
echo "Token response $token_response"
wrapping_token=$(echo $token_response | ./jq -r '.wrap_info.token')
echo $wrapping_token

echo "Set application env..."
##This will rewrite root token
export VAULT_TOKEN="$wrapping_token"
cd ..
echo "Starting jar $WLL_JAR"
java -agentlib:jdwp=transport=dt_socket,server=y,address=5005,suspend=n -jar $WLL_JAR
