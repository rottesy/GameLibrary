# Ответы для защиты (максимально подробная версия)

Ниже максимально развёрнутые ответы по всем пунктам.  
Если термин на английском — сначала он указан на английском, затем объяснение на русском.

---
## 1) ООП (OOP) и применение в проекте

### Encapsulation (Инкапсуляция)
**Encapsulation** — сокрытие внутренних данных/реализаций и предоставление публичного интерфейса.

**В проекте:**
1. Контроллеры не знают, как устроена БД — они вызывают сервисы.
2. Сервисы не раскрывают внутренние сущности наружу — вместо этого используются DTO.
3. Репозитории скрывают SQL‑детали — сервис работает через методы репозитория.

**Зачем:**  
Если завтра изменится структура таблиц, API можно сохранить стабильным.

---
### Inheritance (Наследование)
**Inheritance** — повторное использование кода через наследование.

**В проекте:**
- Репозитории наследуют `JpaRepository`, что даёт базовые методы CRUD.

**Зачем:**  
Меньше кода, стандартное поведение.

---
### Polymorphism (Полиморфизм)
**Polymorphism** — один интерфейс, много реализаций.

**В проекте:**
- Контроллеры используют интерфейсы сервисов (`GameService`), а Spring внедряет реализацию (`GameServiceImpl`).

**Зачем:**  
Можно менять реализацию сервиса без изменения контроллеров.

---
### Abstraction (Абстракция)
**Abstraction** — выделение ключевой логики, скрытие деталей.

**В проекте:**
- Контроллер описывает “что” нужно сделать.
- Сервис решает “как” это сделать.

---
## 2) SOLID, DRY, KISS, YAGNI

### SOLID
1. **Single Responsibility Principle (SRP)**  
   Класс выполняет одну задачу.  
   - Контроллер — HTTP.
   - Сервис — бизнес‑логика.
   - Репозиторий — доступ к данным.

2. **Open/Closed Principle (OCP)**  
   Классы расширяются, но не изменяются.  
   - Можно добавить новый метод в сервис, не ломая старые.

3. **Liskov Substitution Principle (LSP)**  
   Интерфейс можно заменить реализацией.  
   - Контроллер работает с `GameService`.

4. **Interface Segregation Principle (ISP)**  
   Интерфейс не должен быть слишком большим.  
   - Каждый сервис имеет свой компактный интерфейс.

5. **Dependency Inversion Principle (DIP)**  
   Зависимость от абстракций.  
   - Внедрение через Spring DI.

---
### DRY (Don’t Repeat Yourself)
**DRY** — не повторять одно и то же.
- Строки ошибок вынесены в константы.
- Общая логика composite‑сохранения вынесена в один метод.

---
### KISS (Keep It Simple, Stupid)
**KISS** — делать максимально просто.
- Контроллеры тонкие.
- Логика лежит в сервисах.

---
### YAGNI (You Aren’t Gonna Need It)
**YAGNI** — не делать лишнее заранее.
- Реализовано только то, что требует задание.

---
## 3) REST

**REST (Representational State Transfer)** — стиль построения API на основе ресурсов и HTTP‑методов.

### Свойства REST (на англ. → объяснение на русском)
1. **Client–Server** — клиент и сервер разделены. Клиент отвечает за UI, сервер — за данные.  
2. **Stateless** — сервер не хранит состояние сессии клиента, каждый запрос самостоятельный.  
3. **Cacheable** — ответы могут кешироваться (если это разрешено).  
4. **Uniform Interface** — единый интерфейс: ресурсы + HTTP‑методы + статус‑коды.  
5. **Layered System** — архитектура может быть многослойной (прокси, балансировщики).  
6. **Code on Demand (optional)** — сервер может передавать исполняемый код (например JS).

---
## 4) URL vs URI

**URI (Uniform Resource Identifier)** — идентификатор ресурса.  
**URL (Uniform Resource Locator)** — URI с адресом (протокол, домен, путь).

Пример:
- URI: `/api/games/1`
- URL: `http://localhost:8080/api/games/1`

---
## 5) HTTP: структура, методы, статус‑коды

### Структура запроса
1. **Method** — GET/POST/PUT/DELETE...
2. **URL**
3. **Headers**
4. **Body** (если требуется)

### Методы (основные)
- **GET** — получить ресурс (safe, idempotent).
- **POST** — создать ресурс (non‑idempotent).
- **PUT** — заменить ресурс (idempotent).
- **PATCH** — частично изменить ресурс.
- **DELETE** — удалить ресурс (idempotent).
- **HEAD** — как GET, но без тела.
- **OPTIONS** — какие методы доступны.

### Полный перечень основных статус‑кодов

#### 1xx Informational
100 Continue  
101 Switching Protocols  
102 Processing  
103 Early Hints

#### 2xx Success
200 OK  
201 Created  
202 Accepted  
203 Non‑Authoritative Information  
204 No Content  
205 Reset Content  
206 Partial Content  
207 Multi‑Status  
208 Already Reported  
226 IM Used

#### 3xx Redirection
300 Multiple Choices  
301 Moved Permanently  
302 Found  
303 See Other  
304 Not Modified  
305 Use Proxy  
307 Temporary Redirect  
308 Permanent Redirect

#### 4xx Client Errors
400 Bad Request  
401 Unauthorized  
402 Payment Required  
403 Forbidden  
404 Not Found  
405 Method Not Allowed  
406 Not Acceptable  
407 Proxy Authentication Required  
408 Request Timeout  
409 Conflict  
410 Gone  
411 Length Required  
412 Precondition Failed  
413 Payload Too Large  
414 URI Too Long  
415 Unsupported Media Type  
416 Range Not Satisfiable  
417 Expectation Failed  
418 I’m a teapot  
421 Misdirected Request  
422 Unprocessable Entity  
423 Locked  
424 Failed Dependency  
425 Too Early  
426 Upgrade Required  
428 Precondition Required  
429 Too Many Requests  
431 Request Header Fields Too Large  
451 Unavailable For Legal Reasons

#### 5xx Server Errors
500 Internal Server Error  
501 Not Implemented  
502 Bad Gateway  
503 Service Unavailable  
504 Gateway Timeout  
505 HTTP Version Not Supported  
506 Variant Also Negotiates  
507 Insufficient Storage  
508 Loop Detected  
510 Not Extended  
511 Network Authentication Required

---
## 6) Bean и Application Context

**Bean** — объект, управляемый Spring.  
**Application Context** — контейнер, где хранятся бины.

Примеры бинов в проекте:
- `@RestController`
- `@Service`
- `@Repository`
- MapStruct‑Mapper (`componentModel = "spring"`)

---
## 7) @PathVariable, @RequestParam, @RequestBody, @ResponseBody

1. **@PathVariable** — значения из URL пути.  
   Пример: `/api/games/1`

2. **@RequestParam** — query‑параметр.  
   Пример: `/api/games?genre=RPG`

3. **@RequestBody** — JSON из тела запроса.

4. **@ResponseBody** — объект → JSON (в `@RestController` автоматически).

---
## 8) @Controller vs @RestController

**@Controller** — HTML / MVC.  
**@RestController** — JSON / REST (включает `@ResponseBody`).

---
## 9) JSON, сериализация/десериализация

**JSON** — формат обмена данными.  
**Serialization** — объект → JSON.  
**Deserialization** — JSON → объект.

Spring использует Jackson.

---
## 10) PK, FK, виды связей, JOIN

**PK** — уникальный идентификатор записи.  
**FK** — ссылка на другую таблицу.

### Виды связей:
- **One‑to‑Many** — один разработчик → много игр.
- **Many‑to‑Many** — игры ↔ жанры.

### JOIN
JOIN объединяет таблицы по FK, чтобы получить “связанные” данные в одном запросе.

---
## 11) ORM, JPA, Hibernate, Spring Data

**ORM** — связывает объекты и таблицы.  
**JPA** — стандарт ORM.  
**Hibernate** — реализация JPA.  
**Spring Data JPA** — удобные репозитории.

---
## 12) @Entity, @Table, @JoinColumn

- `@Entity` — сущность БД.  
- `@Table` — имя таблицы.  
- `@JoinColumn` — FK.

---
## 13) CascadeType, FetchType

**FetchType.LAZY** — связи грузятся по требованию.  
**CascadeType.ALL + orphanRemoval** — для сущностей, которые живут только в контексте родителя.

---
## 14) N+1

**N+1** — 1 запрос за список + N запросов за связи.

---
## 15) @EntityGraph

**@EntityGraph** — указание, какие связи нужно подгрузить сразу, чтобы избежать N+1.

---
## 16) @Transactional

**@Transactional** — группа операций выполняется как единое целое (commit/rollback).

---
## 17) DTO и Mapper

**DTO** — контракт API.  
**Mapper** — преобразует Entity ↔ DTO.
