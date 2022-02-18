#!/bin/bash

url="http://localhost:8080"
name="string"
password="string"

id=$(./users.sh $name $password | jq '.id' | tr -d "\"")

red=`tput setaf 1`
reset=`tput sgr0`

echo "${red}users/id без токена${reset}"
curl -X GET "http://localhost:8080/users/${id}" | jq '.error, .status, .timestamp, .message, .path'

token=$(./auth.sh $name $password)

echo "${red}users/id c токеном${reset}"
curl -X GET "http://localhost:8080/users/$id" -H  "accept: */*" -H  "Authorization: Bearer $token" | jq '.'

echo "${red}users/id c несуществующим id${reset}"
curl -X GET "http://localhost:8080/users/3fa85f64-5717-4562-b3fc-2c963f66afa6" -H  "accept: */*" -H  "Authorization: Bearer $token" | jq '.error, .status, .timestamp, .message, .path'
