url="http://77.234.215.138:30017"
name="string"
password="string"

red=$(tput setaf 1)
reset=$(tput sgr0)

./users.sh $name $password > /dev/null
token=$(./auth.sh $name $password)

echo "${red}Add product with non zero count${reset}"
curl -sX POST "$url/_internal/catalogItem" -H  "accept: */*" -H  "Content-Type: application/json" -d "{\"title\":\"apple\",\"description\":\"cool\",\"price\":10,\"amount\":25}" | jq '.'
echo "${red}Add product with zero count${reset}"
curl -sX POST "$url/_internal/catalogItem" -H  "accept: */*" -H  "Content-Type: application/json" -d "{\"title\":\"Валенок\",\"description\":\"Шерсть\",\"price\":100,\"amount\":0}" | jq '.'

echo "${red}Get products with non zero count${reset}"
curl -sX GET "$url/items?available=true" -H  "accept: */*" -H  "Authorization: Bearer $token" | jq '.'
echo "${red}Get products with zero count${reset}"
curl -sX GET "$url/items?available=false" -H  "accept: */*" -H  "Authorization: Bearer $token" | jq '.'
