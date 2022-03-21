# Bombardier

Основная дока тут – https://andrsuh.notion.site/cd06c475dcf449018749348e16582ee9

## Кастомизация через application.yml
Для локальной разработки нужно включить профиль `dev`
```yaml
bombardier:
  # Включение/отключение отправки хедера Authorization bearer
  # Действие метода executeWithAuth будет аналогично методу execute, даже если в первый передать токен
  # По умолчанию: true
  auth-enabled: true
  # Список сервисов, который будет доступен для тестирования
  teams:
    - name: "p03" # serviceName, который указывается в запросах к бомбардьеру
      url: "http://p03:8080" # адрес сервиса
    - name: "p04" # ... и так далее
      url: "http://service-304:8080"
```
(`по умолчанию` = указано в application.yml, кастомизация через профили, подробности [тут](https://www.baeldung.com/spring-profiles))