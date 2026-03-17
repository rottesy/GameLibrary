# Обоснование пункта 6: сохранение связанных сущностей, частичное сохранение и откат

Ниже простыми словами объясняется:
1. Что именно сохраняется.
2. Как демонстрируется **частичное сохранение** без транзакции.
3. Как демонстрируется **полный откат** с транзакцией.
4. Где это реализовано в коде (точные места).

## Что такое транзакция (очень просто)

**Транзакция** — это как “один единый пакет действий”.  
Идея простая: **либо всё получилось, либо ничего не записалось**.

Пример в жизни:  
Ты переводишь деньги с карты А на карту Б.
1. Списали с карты А.
2. Зачислили на карту Б.

Если в середине произошла ошибка, правильное поведение — **откатить всё**,  
чтобы деньги не исчезли.

В базе данных транзакция делает то же самое:
1. Внутри транзакции можно выполнить много операций.
2. Если всё успешно — **commit** (сохранить).
3. Если ошибка — **rollback** (откатить).

Без транзакции операции сохраняются **по одной**, и если на середине ошибка — часть данных остаётся в базе.

## Что сохраняем

Мы сохраняем **сразу несколько связанных сущностей**:
1. `Game` (игра)
2. `Review` (отзыв)
3. `Achievement` (достижение)

Это одна операция, которая по логике должна быть целостной.

## Частичное сохранение без `@Transactional`

Если транзакции **нет**, то:
1. `Game` может сохраниться.
2. `Review` может сохраниться.
3. Затем происходит ошибка → `Achievement` не сохраняется.

В итоге в базе остаётся **частично сохранённое** состояние.

### Где это в коде (точные места)

1. **Эндпоинт**  
   `src/main/java/com/example/gamelibrary/controller/GameController.java`  
   Метод `createGameWithReviewAndAchievementNoTx()`  
   URL:  
   ```
   POST /api/games/with-review-and-achievement/no-tx
   ```

2. **Сервис**  
   `src/main/java/com/example/gamelibrary/service/impl/GameServiceImpl.java`  
   Метод:
   ```java
   @Transactional(propagation = Propagation.NOT_SUPPORTED)
   public GameResponse createGameWithReviewAndAchievementNoTx(...)
   ```
   Здесь транзакции **нет** (Propagation.NOT_SUPPORTED).

3. **Общая логика сохранения**  
   `GameServiceImpl.createGameWithReviewAndAchievementInternal(...)`  
   Этот метод сохраняет `Game`, затем `Review`, потом (если нет ошибки) `Achievement`.

## Полный откат с `@Transactional`

Если транзакция **есть**, то:
1. Начинается единая транзакция.
2. Все три объекта сохраняются.
3. Если происходит ошибка — **всё откатывается**.

### Где это в коде (точные места)

1. **Эндпоинт**  
   `src/main/java/com/example/gamelibrary/controller/GameController.java`  
   Метод `createGameWithReviewAndAchievementTx()`  
   URL:
   ```
   POST /api/games/with-review-and-achievement/tx
   ```

2. **Сервис**  
   `src/main/java/com/example/gamelibrary/service/impl/GameServiceImpl.java`  
   Метод:
   ```java
   @Transactional
   public GameResponse createGameWithReviewAndAchievementTx(...)
   ```
   Здесь транзакция **включена**.

3. **Общая логика сохранения**  
   Используется тот же метод:
   ```java
   createGameWithReviewAndAchievementInternal(...)
   ```
   Но теперь он работает **внутри транзакции**.

## Как специально вызвать ошибку

Чтобы показать различие между “no‑tx” и “tx”, мы специально кидаем ошибку,
если в запросе передано:

```
achievement.name = "FAIL"
```

Где это в коде:
1. `GameServiceImpl.shouldFailComposite(...)`  
2. В `createGameWithReviewAndAchievementInternal(...)`:
   ```java
   if (shouldFailComposite(request)) {
       throw new IllegalStateException("Simulated failure after review save");
   }
   ```

## Как это показать на защите (простая демонстрация)

1. Отправь запрос **без транзакции**:
```
POST /api/games/with-review-and-achievement/no-tx
```
2. Передай `achievement.name = "FAIL"`.
3. После ошибки проверь базу:
   - Игра и отзыв **останутся**.
   - Достижения **не будет**.

4. Отправь запрос **с транзакцией**:
```
POST /api/games/with-review-and-achievement/tx
```
5. Передай `achievement.name = "FAIL"`.
6. После ошибки проверь базу:
   - **Ничего не сохранится** (всё откатится).

## Примеры запросов (готовые для копирования)

> В примерах ниже предполагается, что в базе уже есть:
> `developerId = 1`, `genreIds = [1]`, `userId = 1`.

### 1) Без транзакции (частичное сохранение)

**Postman:**
- Method: `POST`
- URL: `http://localhost:8080/api/games/with-review-and-achievement/no-tx`
- Headers: `Content-Type: application/json`
- Body (raw / JSON):
```json
{
  "game": {
    "title": "Tx Demo Game NoTx",
    "description": "demo",
    "releaseDate": "2024-01-01",
    "rating": 7,
    "developerId": 1,
    "genreIds": [1]
  },
  "review": {
    "rating": 7,
    "comment": "ok",
    "gameId": 1,
    "userId": 1
  },
  "achievement": {
    "name": "FAIL",
    "description": "force error",
    "gameId": 1
  }
}
```

```bash
curl -X POST http://localhost:8080/api/games/with-review-and-achievement/no-tx \
  -H "Content-Type: application/json" \
  -d '{
    "game": {
      "title": "Tx Demo Game NoTx",
      "description": "demo",
      "releaseDate": "2024-01-01",
      "rating": 7,
      "developerId": 1,
      "genreIds": [1]
    },
    "review": {
      "rating": 7,
      "comment": "ok",
      "gameId": 1,
      "userId": 1
    },
    "achievement": {
      "name": "FAIL",
      "description": "force error",
      "gameId": 1
    }
  }'
```

### 2) С транзакцией (полный откат)

**Postman:**
- Method: `POST`
- URL: `http://localhost:8080/api/games/with-review-and-achievement/tx`
- Headers: `Content-Type: application/json`
- Body (raw / JSON):
```json
{
  "game": {
    "title": "Tx Demo Game Tx",
    "description": "demo",
    "releaseDate": "2024-01-01",
    "rating": 7,
    "developerId": 1,
    "genreIds": [1]
  },
  "review": {
    "rating": 7,
    "comment": "ok",
    "gameId": 1,
    "userId": 1
  },
  "achievement": {
    "name": "FAIL",
    "description": "force error",
    "gameId": 1
  }
}
```

```bash
curl -X POST http://localhost:8080/api/games/with-review-and-achievement/tx \
  -H "Content-Type: application/json" \
  -d '{
    "game": {
      "title": "Tx Demo Game Tx",
      "description": "demo",
      "releaseDate": "2024-01-01",
      "rating": 7,
      "developerId": 1,
      "genreIds": [1]
    },
    "review": {
      "rating": 7,
      "comment": "ok",
      "gameId": 1,
      "userId": 1
    },
    "achievement": {
      "name": "FAIL",
      "description": "force error",
      "gameId": 1
    }
  }'
```

## Итог

1. **Без транзакции** — сохраняется часть данных (демонстрация частичного сохранения).
2. **С транзакцией** — при ошибке всё откатывается (демонстрация полного отката).
3. Одна и та же логика используется для сохранения, различие только в наличии транзакции.
