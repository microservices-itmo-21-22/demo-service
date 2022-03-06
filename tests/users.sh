url="http://77.234.215.138:30017"

curl -sX POST "$url/users" -H  "accept: */*" -H  "Content-Type: application/json" -d "{\"name\":\"$1\",\"password\":\"$2\"}" | jq '.'
