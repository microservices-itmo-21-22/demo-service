url="http://localhost:8080"

red=$(tput setaf 1)
reset=$(tput sgr0)

echo "${red}Get without token${reset}"
curl -sX GET "$url/items?available=true" | jq -r '.error, .status, .timestamp, .message, .path'

echo "${red}Get with token${reset}"
./catalogItem.sh
