# GameLibrary REST API

Spring Boot приложение для управления библиотекой игр. В проекте реализованы CRUD‑операции, DTO/Mapper, работа с PostgreSQL, демонстрации N+1 и транзакций, а также Checkstyle.

## Быстрый старт

1. Установить JDK 21 и PostgreSQL.
2. Создать БД и пользователя:
```sql
CREATE USER gamelibrary WITH PASSWORD 'gamelibrary';
CREATE DATABASE gamelibrary OWNER gamelibrary;
```
3. Запуск:
```bash
DB_URL=jdbc:postgresql://localhost:5432/gamelibrary \
DB_USERNAME=gamelibrary \
DB_PASSWORD=gamelibrary \
./scripts/run-app.sh
```

## Переменные окружения

Используются в `src/main/resources/application.properties`:
- `DB_URL` (по умолчанию `jdbc:postgresql://localhost:5432/gamelibrary`)
- `DB_USERNAME` (по умолчанию `postgres`)
- `DB_PASSWORD` (по умолчанию `postgres`)

## Проверка Checkstyle

```bash
MAVEN_USER_HOME=/tmp/m2 ./mvnw -q -DskipTests -Dmaven.repo.local=/tmp/m2/repository checkstyle:check
```

## Демонстрации по заданию

### N+1
- Наивный: `GET /api/games/with-reviews/naive`
- Решение: `GET /api/games/with-reviews`

SQL‑логи включены в `src/main/resources/application.properties`.

### Транзакции
- Без транзакции (частичное сохранение):  
  `POST /api/games/with-review-and-achievement/no-tx`
- С транзакцией (откат):  
  `POST /api/games/with-review-and-achievement/tx`

Чтобы сымитировать ошибку: `achievement.name = "FAIL"`.

## Postman

Полная коллекция запросов:
`docs/postman/full-demo.postman_collection.json`

## Документация для защиты

Подробные объяснения:
- `docs/defense-answers.md`
- `docs/cascade-fetch-explanation.md`
- `docs/n-plus-one-explanation.md`
- `docs/transactions-explanation.md`
