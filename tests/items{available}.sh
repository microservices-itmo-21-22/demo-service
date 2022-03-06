url="http://77.234.215.138:30017"

red=$(tput setaf 1)
reset=$(tput sgr0)

echo "${red}Get without token${reset}"
curl -sX GET "$url/items?available=true" | jq -r '.error, .status, .timestamp, .message, .path'

echo "${red}Get with token${reset}"
./catalogItem.sh
