url="http://localhost:8080"
name="string"
password="string"

red=$(tput setaf 1)
reset=$(tput sgr0)

./users.sh $name $password > /dev/null
token=$(./auth.sh $name $password)

echo "${red}Post order without token${reset}"
curl -sX POST "$url/orders" | jq '.error, .status, .timestamp, .message, .path'

echo "${red}Post order with token${reset}"
curl -sX POST "$url/orders" -H "accept: */*" -H  "Authorization: Bearer $token" | jq '.'
