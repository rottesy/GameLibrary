# Ошибки, Валидация, Логирование, AOP и Swagger/OpenAPI

Этот документ нужен как полное объяснение того, что реализовано в проекте по темам:

1. Глобальная обработка ошибок через `@RestControllerAdvice`
2. Валидация входных данных через `@Valid` и `@Validated`
3. Единый JSON-формат ошибок
4. Логирование через `logback`
5. Логирование времени выполнения сервисов через `AOP`
6. Swagger/OpenAPI документация
7. SQL-логи Hibernate в консоли
8. Работа с папкой `logs` как с runtime-артефактом, а не с содержимым git

Документ написан не только как краткий конспект для защиты, но и как подробное объяснение того, как всё это работает внутри проекта, какие файлы за что отвечают, какие сценарии можно показать на демонстрации и какие нюансы есть в текущей реализации.

## 1. Что именно сделано в коде

В этой части проекта реализовано следующее:

- Добавлен глобальный обработчик ошибок `GlobalExceptionHandler`, который централизованно перехватывает типовые ошибки Spring MVC, валидации, преобразования типов, отсутствующих endpoint и неожиданных исключений.
- В DTO запросов добавлены ограничения валидации: `@NotBlank`, `@NotNull`, `@Email`, `@Size`, `@Min`, `@Max`.
- В контроллерах включена валидация не только `@RequestBody`, но и параметров пути и query-параметров за счёт `@Validated`.
- Для ответов с ошибками введены два согласованных DTO:
  - `ErrorResponse` для обычных ошибок
  - `ValidationErrorResponse` для ошибок валидации
- Настроен `logback`:
  - вывод логов в консоль
  - запись в `logs/application.log`
  - запись ошибок уровня `ERROR` в `logs/error.log`
  - ротация и архивация логов в `logs/archived`
- Включён вывод SQL-запросов Hibernate в консоль и в `application.log`.
- Добавлен аспект `ServiceLoggingAspect`, который замеряет время выполнения всех сервисных методов и логирует ошибки сервисного слоя.
- Подключён Swagger/OpenAPI:
  - UI по адресу `/swagger-ui.html`
  - OpenAPI JSON по адресу `/api-docs`
- Папка `logs` добавлена в `.gitignore`, а сами лог-файлы исключены из индекса git, чтобы не хранить runtime-логи в репозитории.
- В `ServiceLoggingAspect` устранено дублирование строкового литерала `"Method {} completed in {} ms"` через отдельную константу.

## 2. Где что находится

Основные файлы этой части проекта:

- `src/main/java/com/example/gamelibrary/exception/GlobalExceptionHandler.java`
- `src/main/java/com/example/gamelibrary/exception/response/ErrorResponse.java`
- `src/main/java/com/example/gamelibrary/exception/response/ValidationErrorResponse.java`
- `src/main/java/com/example/gamelibrary/exception/*NotFoundException.java`
- `src/main/java/com/example/gamelibrary/aop/ServiceLoggingAspect.java`
- `src/main/resources/logback-spring.xml`
- `src/main/resources/application.properties`
- `src/main/java/com/example/gamelibrary/config/OpenApiConfig.java`
- `src/main/java/com/example/gamelibrary/controller/*.java`
- `src/main/java/com/example/gamelibrary/model/dto/request/*.java`
- `pom.xml`
- `.gitignore`

Особенно важные DTO запросов:

- `GameRequest`
- `UserRequest`
- `ReviewRequest`
- `DeveloperRequest`
- `GenreRequest`
- `CollectionRequest`
- `AchievementRequest`
- `GameCompositeRequest`

## 3. Как проходит запрос в текущей архитектуре

Чтобы понять, как связаны все эти части, удобно смотреть на один HTTP-запрос как на цепочку.

### 3.1 Успешный сценарий

1. Клиент отправляет HTTP-запрос в контроллер.
2. Spring пытается собрать параметры и тело запроса.
3. Если на `@RequestBody` стоит `@Valid`, запускается Bean Validation по DTO.
4. Если на контроллере стоит `@Validated`, запускается валидация `@PathVariable` и `@RequestParam`.
5. Если всё валидно, контроллер вызывает сервис.
6. Вызов сервиса перехватывается `ServiceLoggingAspect`.
7. Сервис работает с репозиториями и Hibernate.
8. Hibernate выполняет SQL, и SQL-запросы пишутся в консоль и `application.log`.
9. Сервис возвращает результат.
10. Аспект замеряет время выполнения и пишет лог.
11. Контроллер возвращает DTO ответа клиенту.

### 3.2 Сценарий с ошибкой

1. Если ошибка произошла до сервиса, например на этапе валидации или парсинга JSON, Spring выбрасывает соответствующее исключение.
2. Если ошибка произошла в сервисе, аспект сначала зафиксирует её в логах, затем исключение пойдёт дальше.
3. `GlobalExceptionHandler` перехватит исключение и преобразует его в JSON-ответ с нужным HTTP-статусом.
4. В зависимости от уровня логирования запись попадёт в `application.log`, `error.log` или в оба файла.

Это важно для защиты: здесь не просто “есть обработчик ошибок”, а выстроен цельный pipeline обработки запроса.

## 4. Глобальная обработка ошибок

### 4.1 Почему используется `@RestControllerAdvice`

В проекте используется `@RestControllerAdvice`, а не ручная обработка ошибок в каждом контроллере.

Это даёт несколько преимуществ:

- вся логика ошибок сосредоточена в одном месте
- контроллеры остаются чистыми и читаемыми
- все endpoint возвращают ошибки в согласованном формате
- легко расширять обработку новых случаев

`@RestControllerAdvice` по сути объединяет:

- `@ControllerAdvice`
- автоматический `@ResponseBody`

То есть любой возвращаемый объект автоматически сериализуется в JSON.

### 4.2 Какие ошибки обрабатываются

В `GlobalExceptionHandler` обрабатываются следующие случаи.

#### `MethodArgumentNotValidException`

Когда возникает:

- тело запроса (`@RequestBody`) не проходит валидацию по DTO

Примеры:

- пустой `title` в `GameRequest`
- `email` неправильного формата в `UserRequest`
- `rating = 15` в `ReviewRequest`

Что возвращается:

- HTTP `400 Bad Request`
- `ValidationErrorResponse`

#### `HandlerMethodValidationException`

Когда возникает:

- не проходят ограничения на параметрах контроллера

Примеры:

- `GET /api/games/0`, если `id` помечен `@Positive`
- `GET /api/games/top-rated?limit=101`, если `limit` ограничен `@Max(100)`
- `GET /api/games/search?keyword=`, если `keyword` помечен `@NotBlank`

Что возвращается:

- HTTP `400 Bad Request`
- `ValidationErrorResponse`

#### `ConstraintViolationException`

Это ещё один вариант ошибки валидации, который может появляться в некоторых сценариях Bean Validation.

Что возвращается:

- HTTP `400 Bad Request`
- `ValidationErrorResponse`

#### `MethodArgumentTypeMismatchException`

Когда возникает:

- параметр не удалось преобразовать к нужному типу

Пример:

- `GET /api/games/abc`, где `id` должен быть `Long`

Что возвращается:

- HTTP `400 Bad Request`
- `ErrorResponse`

#### `MissingServletRequestParameterException`

Когда возникает:

- обязательный query-параметр не передан

Пример:

- `GET /api/games/search` без `keyword`

Что возвращается:

- HTTP `400 Bad Request`
- `ErrorResponse`

#### `HttpMessageNotReadableException`

Когда возникает:

- битый JSON
- JSON не соответствует ожидаемой структуре настолько, что Spring не может его разобрать

Пример:

- пропущена закрывающая скобка в теле запроса

Что возвращается:

- HTTP `400 Bad Request`
- `ErrorResponse`
- сообщение `"Malformed JSON request"`

#### `HttpRequestMethodNotSupportedException`

Когда возникает:

- endpoint существует, но вызван не тем HTTP-методом

Пример:

- `PATCH /api/games/1`, если для этого endpoint нет `PATCH`

Что возвращается:

- HTTP `405 Method Not Allowed`
- `ErrorResponse`

#### `NoResourceFoundException`

Когда возникает:

- endpoint не существует вообще

Пример:

- `GET /api/not-existing`

Что возвращается:

- HTTP `404 Not Found`
- `ErrorResponse`
- сообщение `"Endpoint not found"`

#### `DataIntegrityViolationException`

Когда возникает:

- база данных отклонила операцию из-за нарушения ограничений целостности

Что возвращается:

- HTTP `409 Conflict`
- `ErrorResponse`

Важно:

- сам обработчик для такого случая есть
- но в текущей бизнес-логике некоторые операции специально удаляют связи заранее, поэтому не каждый потенциально конфликтный сценарий реально приводит к `409`

#### `ResponseStatusException`

Когда возникает:

- если код явно бросает `ResponseStatusException`

Что возвращается:

- статус берётся из исключения
- тело возвращается как `ErrorResponse`

#### `RuntimeException`

Это общий обработчик runtime-исключений.

Здесь есть важная логика:

- если у исключения есть `@ResponseStatus`, статус берётся оттуда
- если это не 5xx, ошибка логируется как `WARN`
- если это 5xx, ошибка логируется как `ERROR`

Это позволяет корректно обрабатывать пользовательские исключения вроде:

- `GameNotFoundException`
- `UserNotFoundException`
- `DeveloperNotFoundException`
- `GenreNotFoundException`
- `ReviewNotFoundException`
- `CollectionNotFoundException`
- `AchievementNotFoundException`

Все они помечены `@ResponseStatus(HttpStatus.NOT_FOUND)`.

#### `Exception`

Это последний fallback-обработчик.

Если ошибка не попала ни в один из специализированных `@ExceptionHandler`, срабатывает именно он.

Что возвращается:

- HTTP `500 Internal Server Error`
- `ErrorResponse`
- сообщение `"An unexpected error occurred"`

## 5. Единый формат ошибок

### 5.1 Зачем нужны отдельные DTO

Вместо того чтобы возвращать произвольные `Map` или строки, в проекте введены специальные DTO для ошибок.

Это даёт:

- предсказуемый JSON-контракт для клиента
- понятную структуру для фронтенда или Postman
- удобство при защите, потому что можно показать строго определённую модель ответа

### 5.2 `ErrorResponse`

Используется для обычных ошибок.

Поля:

- `status`
- `message`
- `timestamp`

Пример:

```json
{
  "status": 404,
  "message": "Game not found: 999999",
  "timestamp": "2026-03-20T00:10:00"
}
```

### 5.3 `ValidationErrorResponse`

Используется для ошибок валидации.

Поля:

- `status`
- `message`
- `timestamp`
- `errors`

Пример:

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2026-03-20T00:10:00",
  "errors": {
    "title": "must not be blank",
    "rating": "must be less than or equal to 10",
    "developerId": "must not be null"
  }
}
```

### 5.4 Почему это всё равно единый формат

Если преподаватель спросит, почему здесь два DTO, а не один:

- базовый контракт одинаковый: `status`, `message`, `timestamp`
- для ошибок валидации просто добавляется расширенное поле `errors`
- с точки зрения API это единый стиль обработки ошибок, а не хаотичный набор разных ответов

## 6. Валидация входных данных

### 6.1 Что именно валидируется

В проекте валидируются три категории данных:

- тело запроса
- query-параметры
- path-параметры

### 6.2 Валидация тела запроса

Для `@RequestBody` используется `@Valid`.

Примеры:

- `POST /api/games`
- `POST /api/users`
- `POST /api/reviews`
- `PUT /api/games/{id}`
- `POST /api/games/with-review-and-achievement/tx`

Пример ограничений в DTO:

#### `GameRequest`

- `title` обязателен и не может быть пустым
- `title` ограничен по длине `200`
- `description` ограничен по длине `2000`
- `rating` должен быть от `1` до `10`
- `developerId` обязателен

#### `UserRequest`

- `username` обязателен и не может быть пустым
- `username` ограничен по длине `50`
- `email` обязателен
- `email` должен быть корректного формата
- `email` ограничен по длине `255`

#### `ReviewRequest`

- `rating` обязателен
- `rating` должен быть от `1` до `10`
- `comment` ограничен по длине `2000`
- `gameId` обязателен
- `userId` обязателен

### 6.3 Валидация параметров endpoint

На контроллерах стоит `@Validated`, поэтому ограничения работают и на параметрах:

- `@Positive`
- `@Min`
- `@Max`
- `@NotBlank`

Примеры:

- `@PathVariable("id") @Positive Long id`
- `@RequestParam(name = "limit") @Min(1) @Max(100) Integer limit`
- `@RequestParam(name = "keyword") @NotBlank String keyword`

### 6.4 Вложенная валидация

Особенно важен `GameCompositeRequest`.

В нём три вложенных объекта:

- `game`
- `review`
- `achievement`

Каждое из этих полей:

- обязательно (`@NotNull`)
- валидируется рекурсивно (`@Valid`)

Это значит:

- если внутри `game` пустой `title`, весь запрос не пройдёт
- если внутри `review` отсутствует `userId`, весь запрос не пройдёт
- если внутри `achievement` пустой `name`, весь запрос не пройдёт

### 6.5 Важный нюанс composite endpoint

В текущей реализации `ReviewRequest` и `AchievementRequest` внутри composite-запроса всё равно требуют поля `gameId`, потому что DTO помечены `@NotNull`.

Но внутри сервиса эти значения потом переписываются:

- для review ставится только что созданная игра
- для achievement тоже ставится только что созданная игра

То есть для демонстрации composite endpoint поля `review.gameId` и `achievement.gameId` нужно передать, чтобы пройти валидацию, но фактически сервис их заменяет.

Это не ошибка логирования или обработки, а особенность текущей модели DTO.

## 7. Логирование через Logback

### 7.1 Общая схема

Конфигурация логирования находится в `src/main/resources/logback-spring.xml`.

В проекте настроены следующие appenders:

- `CONSOLE` для вывода в консоль
- `FILE` для `logs/application.log`
- `ERROR_FILE` для `logs/error.log`

### 7.2 Какие файлы логов есть

- `logs/application.log` — общий рабочий лог приложения
- `logs/error.log` — только ошибки уровня `ERROR`
- `logs/archived` — архивы после ротации

### 7.3 Как работает `application.log`

В `application.log` пишется основной поток логов приложения:

- `INFO`
- `WARN`
- `ERROR`
- `DEBUG` для пакета `com.example.gamelibrary`

Туда попадают:

- обычные рабочие логи приложения
- предупреждения валидации
- предупреждения обработчика ошибок
- логи AOP
- SQL-запросы Hibernate

### 7.4 Как работает `error.log`

`error.log` настроен через `ThresholdFilter` на уровень `ERROR`.

Это значит:

- `WARN` туда не попадёт
- `INFO` туда не попадёт
- `DEBUG` туда не попадёт
- попадёт только `ERROR`

Это главный ответ на вопрос, почему одни ошибки видны в `application.log`, а в `error.log` нет.

### 7.5 Почему `400 Bad Request` обычно не попадает в `error.log`

Потому что в `GlobalExceptionHandler` такие случаи логируются как `WARN`, а не как `ERROR`.

Например:

- ошибка валидации body
- ошибка валидации query/path параметров
- битый JSON
- неправильный HTTP method
- несуществующий endpoint

Все они логируются как предупреждения.

Поэтому:

- в `application.log` они есть
- в `error.log` их нет

### 7.6 Какие случаи попадают в `error.log`

В текущей конфигурации туда попадают:

- неожиданные `500 Internal Server Error`
- любые исключения, которые аспект `ServiceLoggingAspect` поймал при выполнении сервисного метода и залогировал как `ERROR`

Из-за этого есть важный нюанс.

Например, запрос:

```bash
curl -i http://localhost:8080/api/games/999999
```

вернёт клиенту `404 Not Found`, но в `error.log` запись всё равно может появиться.

Почему:

- сам `GlobalExceptionHandler` для `404` пишет `WARN`
- но сервис выбрасывает исключение
- аспект вокруг сервиса логирует падение метода как `ERROR`

То есть в `error.log` попадает не “HTTP-статус 404 как таковой”, а факт того, что сервисный метод завершился исключением.

### 7.7 Почему `GET /api/not-existing` обычно не попадает в `error.log`

Потому что в этом случае:

- сервис вообще не вызывается
- аспект не срабатывает
- остаётся только `GlobalExceptionHandler`
- он логирует как `WARN`

Итог:

- запрос вернёт `404`
- запись будет в `application.log`
- в `error.log` её, скорее всего, не будет

### 7.8 SQL-логи Hibernate

Сейчас SQL-запросы включены и видны в консоли.

Как это сделано:

- в `application.properties` включено форматирование SQL:
  - `spring.jpa.properties.hibernate.format_sql=true`
- в `logback-spring.xml` логгер `org.hibernate.SQL` поднят до `DEBUG`

Что это даёт:

- Hibernate печатает сами SQL-запросы
- запросы видны в консоли
- запросы также попадают в `application.log`

### 7.9 Почему параметры SQL не печатаются

Параметры bind сейчас не включены на подробном уровне.

Логгеры:

- `org.hibernate.type.descriptor.sql.BasicBinder`
- `org.hibernate.orm.jdbc.bind`

остаются на уровне `ERROR`.

Значит:

- сами SQL-запросы видны
- значения параметров для `?` обычно не видны

Это сделано осознанно, чтобы не засорять лог и не получать слишком многословный вывод.

### 7.10 Почему SQL не попадает в `error.log`

У Hibernate SQL-логгеров стоит `additivity="false"` и они направлены только в:

- `CONSOLE`
- `FILE`

Они не направлены в `ERROR_FILE`.

Поэтому:

- SQL идёт в консоль
- SQL идёт в `application.log`
- SQL не идёт в `error.log`

### 7.11 Ротация логов

Для файловых appender'ов используется `SizeAndTimeBasedRollingPolicy`.

Управляющие параметры:

- `LOGS`
- `MAX_FILE_SIZE`
- `MAX_HISTORY`
- `TOTAL_SIZE_CAP`

Что это означает:

- файл ротируется не бесконечно
- старые файлы архивируются
- архивы хранятся ограниченное время
- суммарный объём логов тоже ограничивается

Это полезно и для локальной разработки, и для демонстрации понимания production-подхода.

## 8. AOP для логирования сервисов

### 8.1 Зачем здесь нужен аспект

Если писать логирование времени выполнения вручную в каждом сервисе, получится дублирование кода.

AOP позволяет вынести техническую инфраструктурную задачу из бизнес-логики.

Плюсы такого подхода:

- сервисы остаются чистыми
- поведение едино для всех сервисов
- легко менять пороги и формат логов в одном месте

### 8.2 Что именно перехватывается

В `ServiceLoggingAspect` есть pointcut:

```java
@Pointcut("within(@org.springframework.stereotype.Service *)")
```

Это означает:

- перехватываются классы, помеченные `@Service`
- логика применяется ко всем сервисным методам этих классов

### 8.3 Что делает `@Around`

Аспект работает как оболочка вокруг метода сервиса:

1. Формирует имя метода
2. Запускает `StopWatch`
3. Логирует вход в метод на уровне `DEBUG`
4. Выполняет реальный метод через `joinPoint.proceed()`
5. После успешного завершения считает длительность
6. Пишет лог о времени выполнения
7. Если метод упал, пишет `ERROR` и пробрасывает исключение дальше

### 8.4 Пороги производительности

В текущей реализации:

- больше `500 ms` — `INFO`
- больше `1000 ms` — `WARN`
- всё, что быстрее, — `DEBUG`

Это позволяет:

- не засорять лог предупреждениями на каждый быстрый вызов
- выделять потенциально медленные операции

### 8.5 Поведение при исключении

Если сервисный метод падает:

- аспект пишет лог `ERROR`
- исключение не проглатывается
- оно пробрасывается дальше в `GlobalExceptionHandler`

Именно поэтому один и тот же запрос иногда виден сразу в двух местах:

- `ServiceLoggingAspect` пишет `ERROR`
- `GlobalExceptionHandler` пишет `WARN` или `ERROR` в зависимости от типа ошибки

### 8.6 Небольшое улучшение по качеству кода

В `ServiceLoggingAspect` строка:

```java
"Method {} completed in {} ms"
```

раньше дублировалась несколько раз.

Теперь она вынесена в константу:

```java
private static final String METHOD_COMPLETED_IN_MS = "Method {} completed in {} ms";
```

Это улучшает:

- читаемость
- поддержку
- соответствие требованиям статического анализа

## 9. Swagger/OpenAPI

### 9.1 Что подключено

В `pom.xml` добавлена зависимость:

- `org.springdoc:springdoc-openapi-starter-webmvc-ui`

Также присутствует:

- `spring-boot-starter-aop`

для AOP-аспекта.

### 9.2 Где настраивается OpenAPI

Есть класс `OpenApiConfig`, который создаёт bean `OpenAPI`.

Там задаются:

- title
- version
- description

Это влияет на то, как API выглядит в Swagger UI и в JSON-документации.

### 9.3 Где задаются URL

В `application.properties` настроено:

- `springdoc.api-docs.path=/api-docs`
- `springdoc.swagger-ui.path=/swagger-ui.html`

Итоговые URL:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

### 9.4 Что документируется аннотациями

В контроллерах используются:

- `@Tag`
- `@Operation`
- `@ApiResponse`

В DTO используются:

- `@Schema`

Это даёт:

- человекочитаемую документацию endpoint
- описание входных DTO
- описание кодов ответов

## 10. Что было изменено по логам и инфраструктуре дополнительно

Кроме базовой реализации, были сделаны ещё такие практические улучшения.

### 10.1 SQL-запросы выведены в консоль

Логгер `org.hibernate.SQL` переведён на уровень `DEBUG`, поэтому теперь SQL виден при выполнении запросов.

Это удобно для:

- демонстрации работы JPA/Hibernate
- показа N+1
- анализа транзакционных сценариев
- понимания того, какие запросы реально идут в базу

### 10.2 Папка `logs` убрана из git

Runtime-логи не должны храниться в репозитории, поэтому:

- в `.gitignore` добавлено правило `/logs/`
- файлы логов убраны из индекса git

Зачем это нужно:

- лог-файлы постоянно меняются и засоряют диффы
- это не исходный код
- архивы логов особенно не должны храниться в репозитории

### 10.3 Что важно помнить

`.gitignore` влияет только на неотслеживаемые файлы.

Если лог уже был закоммичен раньше, одного `.gitignore` недостаточно.

Нужно дополнительно убрать его из индекса:

```bash
git rm --cached -r logs
```

После этого:

- файл остаётся локально
- git перестаёт его отслеживать

## 11. Как подготовить проект к демонстрации

### 11.1 Поднять базу

По README:

```sql
CREATE USER gamelibrary WITH PASSWORD 'gamelibrary';
CREATE DATABASE gamelibrary OWNER gamelibrary;
```

### 11.2 Запустить приложение

```bash
DB_URL=jdbc:postgresql://localhost:5432/gamelibrary \
DB_USERNAME=gamelibrary \
DB_PASSWORD=gamelibrary \
./scripts/run-app.sh
```

После запуска проверить:

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/api-docs`

### 11.3 Подготовить минимальные данные

Для некоторых демонстраций нужны валидные сущности.

Сначала удобно создать разработчика:

```bash
curl -i -X POST http://localhost:8080/api/developers \
  -H "Content-Type: application/json" \
  -d '{"name":"CD Projekt Red"}'
```

Потом жанр:

```bash
curl -i -X POST http://localhost:8080/api/genres \
  -H "Content-Type: application/json" \
  -d '{"name":"RPG"}'
```

Потом пользователя:

```bash
curl -i -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com"}'
```

Важно:

- в дальнейших запросах используй реальные `id`, которые вернутся в ответах
- в примерах ниже условно предполагается, что это `1`

## 12. Подробные сценарии демонстрации

Ниже готовые сценарии, которые можно прямо показывать во время защиты.

### 12.1 Показать Swagger UI

Открыть:

- `http://localhost:8080/swagger-ui.html`

Что показать:

- список контроллеров
- описание endpoint
- DTO-схемы
- коды ответов

Что говорить:

- документация генерируется автоматически через springdoc
- контроллеры описаны аннотациями `@Tag`, `@Operation`, `@ApiResponse`
- DTO описаны через `@Schema`

### 12.2 Показать body-валидацию

Отправить:

```bash
curl -i -X POST http://localhost:8080/api/games \
  -H "Content-Type: application/json" \
  -d '{
    "title": "",
    "description": "bad request",
    "rating": 11,
    "developerId": null
  }'
```

Что произойдёт:

- `@Valid` проверит `GameRequest`
- будет выброшен `MethodArgumentNotValidException`
- `GlobalExceptionHandler` вернёт `400`
- в ответе будет `ValidationErrorResponse`

Что показать:

- JSON-ответ с полями `status`, `message`, `timestamp`, `errors`
- запись в `application.log`

Что важно объяснить:

- невалидные данные не доходят до сервиса и базы
- ошибка централизованно обрабатывается в одном месте

### 12.3 Показать валидацию path-параметра

Отправить:

```bash
curl -i http://localhost:8080/api/games/0
```

Что произойдёт:

- `@Positive` не пропустит значение `0`
- вернётся `400`

Это хороший пример того, что валидируется не только тело запроса.

### 12.4 Показать валидацию query-параметра

Отправить:

```bash
curl -i "http://localhost:8080/api/games/top-rated?limit=101"
```

Что произойдёт:

- параметр `limit` ограничен `@Max(100)`
- вернётся `400`

Можно дополнительно показать:

```bash
curl -i "http://localhost:8080/api/games/search?keyword="
```

### 12.5 Показать ошибку преобразования типа

Отправить:

```bash
curl -i http://localhost:8080/api/games/abc
```

Что произойдёт:

- Spring не сможет преобразовать `abc` в `Long`
- вернётся `400`
- это уже не ошибка `@Valid`, а ошибка преобразования аргумента

### 12.6 Показать битый JSON

Отправить:

```bash
curl -i -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com"'
```

Что произойдёт:

- сработает `HttpMessageNotReadableException`
- вернётся `400`
- сообщение будет `"Malformed JSON request"`

### 12.7 Показать `404 Not Found` для сущности

Отправить:

```bash
curl -i http://localhost:8080/api/games/999999
```

Что произойдёт:

- сервис выбросит `GameNotFoundException`
- клиент получит `404`
- в `application.log` будет предупреждение обработчика
- в `error.log` может появиться запись от `ServiceLoggingAspect`, потому что сервисный метод завершился исключением

Это очень полезный кейс, потому что он одновременно показывает:

- пользовательское исключение
- глобальную обработку ошибок
- работу аспекта
- различие между HTTP-статусом и уровнем внутреннего логирования

### 12.8 Показать `404` для несуществующего endpoint

Отправить:

```bash
curl -i http://localhost:8080/api/not-existing
```

Что произойдёт:

- вернётся `404`
- обработчик вернёт `"Endpoint not found"`
- в `error.log` записи обычно не будет, потому что сервис не вызывался

### 12.9 Показать `405 Method Not Allowed`

Отправить:

```bash
curl -i -X PATCH http://localhost:8080/api/games/1
```

Что произойдёт:

- endpoint найден, но `PATCH` не поддерживается
- вернётся `405`

### 12.10 Показать нормальную успешную запись и SQL

Создать игру:

```bash
curl -i -X POST http://localhost:8080/api/games \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Witcher 3",
    "description": "RPG demo",
    "releaseDate": "2015-05-19",
    "rating": 9,
    "developerId": 1,
    "genreIds": [1]
  }'
```

Что показать:

- в консоли SQL `insert`
- тот же SQL в `application.log`
- время выполнения метода в логах аспекта

Что говорить:

- запрос прошёл контроллер, валидацию, сервис, репозиторий и Hibernate
- SQL действительно видно, значит логирование ORM включено корректно

### 12.11 Показать работу AOP на обычном сервисном вызове

Отправить:

```bash
curl -s http://localhost:8080/api/games > /dev/null
```

Что показать:

- лог входа в сервисный метод
- лог завершения с временем выполнения

Что говорить:

- время выполнения собирается централизованно
- код сервисов не засорён ручным `StopWatch`

### 12.12 Показать ошибку, которая точно попадёт в `error.log`

Можно использовать один из вариантов:

#### Вариант A. `GET /api/games/999999`

Плюсы:

- быстро
- не требует сложной подготовки
- обычно пишет в `error.log` через аспект

#### Вариант B. Искусственная `500` в composite endpoint

Сначала убедиться, что существуют:

- developer
- genre
- user

Потом выполнить:

```bash
curl -i -X POST http://localhost:8080/api/games/with-review-and-achievement/tx \
  -H "Content-Type: application/json" \
  -d '{
    "game": {
      "title": "Tx Demo",
      "description": "Rollback demo",
      "releaseDate": "2024-01-01",
      "rating": 8,
      "developerId": 1,
      "genreIds": [1]
    },
    "review": {
      "rating": 9,
      "comment": "Great game",
      "gameId": 1,
      "userId": 1
    },
    "achievement": {
      "name": "FAIL",
      "description": "Force rollback",
      "gameId": 1
    }
  }'
```

Что произойдёт:

- внутри сервиса после сохранения review будет брошено `IllegalStateException`
- клиент получит `500`
- запись точно попадёт в `error.log`

Почему `name = "FAIL"`:

- в сервисе есть специальная проверка `shouldFailComposite`
- если имя достижения равно `FAIL`, выбрасывается искусственная ошибка

### 12.13 Показать SQL на более сложном чтении

Для демонстрации SQL-логов и связанных тем удобно вызвать:

```bash
curl -s http://localhost:8080/api/games/with-reviews/naive > /dev/null
curl -s http://localhost:8080/api/games/with-reviews > /dev/null
```

Что показать:

- SQL в консоли
- разницу в количестве запросов

Это уже пересекается с темой N+1, но очень удобно демонстрирует, зачем вообще включались SQL-логи.

## 13. Чем отличаются `application.log` и `error.log`

Это один из самых частых вопросов, поэтому удобнее запомнить отдельно.

### `application.log`

Сюда идут:

- обычные информационные логи
- предупреждения
- ошибки
- SQL-запросы Hibernate
- логи аспектов

Если кратко:

- это основной рабочий лог приложения

### `error.log`

Сюда идут:

- только логи уровня `ERROR`

Если кратко:

- это специализированный лог для серьёзных ошибок

### Практические выводы

- `400` из валидации обычно только в `application.log`
- `404` для несуществующего endpoint обычно только в `application.log`
- сервисные исключения часто дают запись и в `error.log`, потому что их дополнительно логирует аспект
- неожиданные `500` почти всегда будут в `error.log`

## 14. Что открыть в коде на защите

Если преподаватель попросит показать реализацию в коде, открывай в таком порядке:

1. `src/main/java/com/example/gamelibrary/exception/GlobalExceptionHandler.java`
2. `src/main/java/com/example/gamelibrary/exception/response/ErrorResponse.java`
3. `src/main/java/com/example/gamelibrary/exception/response/ValidationErrorResponse.java`
4. `src/main/java/com/example/gamelibrary/model/dto/request/GameRequest.java`
5. `src/main/java/com/example/gamelibrary/model/dto/request/UserRequest.java`
6. `src/main/java/com/example/gamelibrary/model/dto/request/GameCompositeRequest.java`
7. `src/main/java/com/example/gamelibrary/controller/GameController.java`
8. `src/main/java/com/example/gamelibrary/controller/UserController.java`
9. `src/main/java/com/example/gamelibrary/aop/ServiceLoggingAspect.java`
10. `src/main/resources/logback-spring.xml`
11. `src/main/resources/application.properties`
12. `src/main/java/com/example/gamelibrary/config/OpenApiConfig.java`
13. `pom.xml`
14. `.gitignore`

## 15. Короткий текст для устного объяснения

Можно объяснить так:

> В проекте я централизовал обработку ошибок через `@RestControllerAdvice`, чтобы все контроллеры возвращали ошибки в одном JSON-формате.  
> Для валидации входных данных использованы `@Valid` и `@Validated`, поэтому проверяются и body, и параметры endpoint.  
> Для логирования настроен `logback`: есть консоль, основной лог приложения, отдельный `error.log` и ротация архивов.  
> Также я добавил AOP-аспект, который логирует время выполнения сервисных методов и ошибки сервисного слоя без вмешательства в бизнес-логику.  
> Для удобства демонстрации и разработки включён вывод SQL Hibernate в консоль, а для документации API подключён Swagger/OpenAPI.  
> Runtime-логи вынесены из git через `.gitignore`, чтобы репозиторий не засорялся рабочими файлами приложения.

## 16. Короткие ответы на частые вопросы

### Почему `400` не пишется в `error.log`?

Потому что это `WARN`, а `error.log` принимает только `ERROR`.

### Почему `GET /api/games/999999` иногда пишет в `error.log`, хотя клиент получает `404`?

Потому что сервис выбросил исключение, а аспект залогировал падение сервисного метода как `ERROR`.

### Почему есть два формата ошибки?

По сути формат один, но для валидации добавлено поле `errors`, чтобы клиент видел подробности по полям.

### Почему SQL виден в консоли, но значения параметров не печатаются?

Потому что включён `org.hibernate.SQL`, но bind-логгеры параметров не подняты до подробного уровня.

### Почему лог-файлы добавлены в `.gitignore`?

Потому что это runtime-артефакты, а не исходный код.

## 17. Итог

По темам ошибок, валидации, логирования, AOP и Swagger проект реализован полноценно:

- ошибки централизованы
- валидация покрывает body и параметры
- формат ответов на ошибки предсказуемый
- логи разделены по назначению
- SQL видно в консоли
- время сервисных методов измеряется автоматически
- документация API доступна через Swagger/OpenAPI
- папка логов не засоряет репозиторий

Если нужно показывать это на защите в одном коротком сценарии, самый удобный порядок такой:

1. Открыть Swagger UI
2. Показать `400` на невалидном `POST`
3. Показать `404` на несуществующей сущности
4. Показать разницу между `application.log` и `error.log`
5. Показать `ServiceLoggingAspect`
6. Показать SQL в консоли на любом `GET` или `POST`
