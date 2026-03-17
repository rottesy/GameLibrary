# Обоснование пункта 5: проблема N+1 и её решение через `@EntityGraph`

Ниже простыми словами объясняется:
1. Что такое проблема N+1.
2. Где она возникает в этом проекте.
3. Как мы её демонстрируем.
4. Как она решена через `@EntityGraph`.

## Что такое проблема N+1

Допустим, ты хочешь получить список **всех игр** и для каждой игры — **её отзывы**.

Если код устроен наивно:
1. Делается **1 запрос** за всеми играми.
2. Потом **для каждой игры** делается ещё **1 запрос** за отзывами.

Если игр 10, то будет:
1 (за игры) + 10 (за отзывы) = **11 запросов**  
Если игр 100, то будет 101 запрос.

Это и есть проблема **N+1**:  
`1` основной запрос + `N` дополнительных запросов.

## Почему это плохо

1. **Много запросов** — медленнее работает приложение.
2. **Нагрузка на базу** — больше времени и ресурсов.
3. **Плохая масштабируемость** — чем больше данных, тем хуже.

## Где возникает в проекте

В нашем проекте это возможно, когда мы:
1. Берём список `Game`.
2. Для каждой `Game` вытаскиваем `reviews` (или `achievements`).

Потому что связи у нас `LAZY`:
```java
@OneToMany(fetch = FetchType.LAZY)
private Set<Review> reviews;
```
Это значит, что отзывы не грузятся сразу, а подгружаются отдельно при доступе.

## Как мы демонстрируем N+1

Для этого есть **наивный** эндпоинт:

```
GET /api/games/with-reviews/naive
```

Он берёт список игр (`findAll()`) и затем в коде обращается к `game.getReviews()` для каждой игры.  
Это как раз и вызывает **N+1**.

Реализация (точные места в коде):
1. `src/main/java/com/example/gamelibrary/controller/GameController.java`  
   Метод `getAllWithReviewsNaive()` — эндпоинт `GET /api/games/with-reviews/naive`.
2. `src/main/java/com/example/gamelibrary/service/GameService.java`  
   Метод `findAllWithReviewsNaive()`.
3. `src/main/java/com/example/gamelibrary/service/impl/GameServiceImpl.java`  
   Метод `findAllWithReviewsNaive()`:
```java
return gameRepository.findAll().stream()
        .map(this::toGameWithReviewsResponse)
        .toList();
```
4. `src/main/java/com/example/gamelibrary/service/impl/GameServiceImpl.java`  
   Метод `toGameWithReviewsResponse(...)` — внутри вызывается `game.getReviews()`
   → Hibernate делает отдельный запрос для каждой игры.

## Как мы решаем N+1 через `@EntityGraph`

Мы добавили специальные методы в репозиторий, которые говорят Hibernate:
**"грузи игры сразу вместе с их отзывами/достижениями"**.

```java
@EntityGraph(attributePaths = "reviews")
@Query("select g from Game g")
List<Game> findAllWithReviews();
```

```java
@EntityGraph(attributePaths = "achievements")
@Query("select g from Game g")
List<Game> findAllWithAchievements();
```

Точные места в коде:
1. `src/main/java/com/example/gamelibrary/repository/GameRepository.java`  
   Методы `findAllWithReviews()` и `findAllWithAchievements()`.
2. `src/main/java/com/example/gamelibrary/controller/GameController.java`  
   Эндпоинты:
   - `GET /api/games/with-reviews`
   - `GET /api/games/with-achievements`
3. `src/main/java/com/example/gamelibrary/service/impl/GameServiceImpl.java`  
   Методы:
   - `findAllWithReviews()`  
   - `findAllWithAchievements()`

Это значит:
1. Запрашиваем игры.
2. Hibernate сразу подгружает нужные связи.
3. В итоге запросов **значительно меньше**.

Для этого есть оптимизированные эндпоинты:
```
GET /api/games/with-reviews
GET /api/games/with-achievements
```

## Итог

1. **N+1 демонстрируется** через `/api/games/with-reviews/naive`.
2. **Решение** сделано через `@EntityGraph`:
   - `/api/games/with-reviews`
   - `/api/games/with-achievements`
3. Результат: меньше SQL‑запросов, быстрее работа приложения и меньше нагрузка на базу.
