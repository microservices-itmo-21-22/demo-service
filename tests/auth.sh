url="http://localhost:8080"

refreshToken=$(curl -sX POST "$url/authentication" -H  "accept: */*" -H  "Content-Type: application/json" -d "{\"name\":\"$1\",\"password\":\"$2\"}" | jq -r '.refreshToken')
curl -sX POST "http://localhost:8080/authentication/refresh" -H  "accept: */*" -H  "Authorization: Bearer $refreshToken" | jq -r '.accessToken'
