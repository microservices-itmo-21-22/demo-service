url="http://localhost:8080"
name="string"
password="string"

red=$(tput setaf 1)
reset=$(tput sgr0)

./users.sh $name $password > /dev/null
token=$(./auth.sh $name $password)

id=$(curl -sX POST "$url/orders" -H "accept: */*" -H  "Authorization: Bearer $token" | jq '.id' | tr -d "\"")

echo "${red}Get order without token${reset}"
curl -sX GET "$url/orders/$id" | jq '.error, .status, .timestamp, .message, .path'

echo "${red}Get non-existent order${reset}"
curl -sX GET "$url/orders/3fa85f64-5717-4562-b3fc-2c963f66afa6" -H "accept: */*" -H  "Authorization: Bearer $token" | jq '.error, .status, .timestamp, .message, .path'

echo "${red}Get current order${reset}"
curl -sX GET "$url/orders/$id" -H "accept: */*" -H  "Authorization: Bearer $token" | jq '.'

echo $token