# Bombardier

Основная дока тут – https://andrsuh.notion.site/cd06c475dcf449018749348e16582ee9

## Кастомизация через application.yml

```yaml
bombardier:
  auth-enabled: true # true запросы будут слаться с Authorization bearer, false этот хедер слаться не будет (если даже передать токен авторизации, он будет игнорироваться)
  teams:
    - name: "p03" # serviceName, который указывается в запросах к бомбардьеру
      url: "http://p03:8080" # адрес сервиса
    - name: "p04" # ... и так далее
      url: "http://service-304:8080"
```