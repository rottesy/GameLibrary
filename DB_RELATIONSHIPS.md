# Связи в БД (по JPA-моделям)

Это описание построено по аннотациям в сущностях `src/main/java/com/example/gamelibrary/model/entity/*.java`. Оно отражает, как приложение ожидает структуру БД (таблицы, внешние ключи, join-таблицы, владение связями, каскады и т.д.). Если в проекте есть отдельные миграции (Liquibase/Flyway) или ручной DDL, то они могут уточнять/переопределять нюансы.

**Сущности и таблицы**
User → `users`  
Game → `games`  
Developer → `developers`  
Genre → `genres`  
Review → `reviews`  
Collection → `collections`  
Achievement → `achievements`

**Общие правила**
Все связи помечены `fetch = LAZY`, поэтому связанные данные не подгружаются автоматически без явного обращения.  
Там, где указаны `cascade = ALL` и `orphanRemoval = true`, жизненный цикл дочерних записей управляется родителем (удаление из коллекции = удаление строки в БД).

**Связи (подробно)**
1. **Developer 1—N Game**  
Один разработчик может иметь много игр, у игры — один разработчик.  
FK: `games.developer_id` → `developers.id`. `nullable` не задан, значит колонка допускает `NULL`.  
Владелец связи: `Game.developer` (сторона `@ManyToOne`).  
Обратная сторона: `Developer.games` (`@OneToMany(mappedBy = "developer")`).  
Каскады не заданы, удаление разработчика не удалит игры автоматически.

2. **Game N—N Genre**  
Игра может иметь много жанров, жанр может относиться к многим играм.  
Join‑таблица: `game_genres` (`game_id`, `genre_id`).  
Владелец связи: `Game.genres` (`@JoinTable`).  
Обратная сторона: `Genre.games` (`mappedBy = "genres"`).  
Каскадов нет.

3. **Game 1—N Review**  
У игры много отзывов, у отзыва одна игра.  
FK: `reviews.game_id` → `games.id`, `nullable = false`.  
Владелец связи: `Review.game`.  
Обратная сторона: `Game.reviews` (`mappedBy = "game"`).  
`Game.reviews` использует `cascade = ALL` и `orphanRemoval = true`, значит отзывы живут вместе с игрой.

4. **User 1—N Review**  
У пользователя много отзывов, у отзыва один пользователь.  
FK: `reviews.user_id` → `users.id`, `nullable = false`.  
Владелец связи: `Review.user`.  
Обратная сторона: `User.reviews` (`mappedBy = "user"`).  
Каскадов нет.

5. **Game 1—N Achievement**  
У игры много достижений, у достижения одна игра.  
FK: `achievements.game_id` → `games.id`, `nullable = false`.  
Владелец связи: `Achievement.game`.  
Обратная сторона: `Game.achievements` (`mappedBy = "game"`).  
`Game.achievements` использует `cascade = ALL` и `orphanRemoval = true`.

6. **User 1—N Collection**  
У пользователя много коллекций, у коллекции один владелец.  
FK: `collections.owner_id` → `users.id`, `nullable = false`.  
Владелец связи: `Collection.owner`.  
Обратная сторона: `User.collections` (`mappedBy = "owner"`).  
`User.collections` использует `cascade = ALL` и `orphanRemoval = true`, коллекции считаются «дочерними» для пользователя.

7. **Collection N—N Game**  
Коллекция содержит много игр, игра может быть в многих коллекциях.  
Join‑таблица: `collection_games` (`collection_id`, `game_id`).  
Владелец связи: `Collection.games` (`@JoinTable`).  
Обратная сторона: `Game.collections` (`mappedBy = "games"`).  
Каскадов нет.

8. **User N—N Game (library)**  
«Библиотека» пользователя содержит много игр, одна игра может быть в библиотеках многих пользователей.  
Join‑таблица: `user_games` (`user_id`, `game_id`).  
Владелец связи: `User.libraryGames` (`@JoinTable`).  
Обратная сторона: `Game.owners` (`mappedBy = "libraryGames"`).  
Каскадов нет.

9. **User N—N Game (wishlist)**  
«Wishlist» пользователя содержит много игр, одна игра может быть в wishlists многих пользователей.  
Join‑таблица: `user_wishlist` (`user_id`, `game_id`).  
Владелец связи: `User.wishlistGames` (`@JoinTable`).  
Обратная сторона: `Game.wishlistedBy` (`mappedBy = "wishlistGames"`).  
Каскадов нет.

**Итог**
Проект использует 4 таблицы-связки (`game_genres`, `collection_games`, `user_games`, `user_wishlist`) и 5 внешних ключей (`games.developer_id`, `reviews.game_id`, `reviews.user_id`, `achievements.game_id`, `collections.owner_id`). Связи настроены на LAZY-загрузку, а каскады с удалением включены только там, где сущность считается «дочерней» (reviews/achievements у игры, collections у пользователя).
