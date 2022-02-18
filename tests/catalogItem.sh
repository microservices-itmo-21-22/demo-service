#!/bin/bash

url="http://localhost:8080"
name="string"
password="string"

./users.sh $name $password > /dev/null
token=$(./auth.sh $name $password)

curl -X POST "$url/_internal/catalogItem" -H  "accept: */*" -H  "Content-Type: application/json" -d "{\"title\":\"apple\",\"description\":\"cool\",\"price\":10,\"amount\":25}" | jq '.'

curl -X GET "$url/items?available=true" -H  "accept: */*" -H  "Authorization: Bearer $token" | jq '.'


