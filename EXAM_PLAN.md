# План доведения проекта до экзамена (MVC `app`, без `rest`)

> Цель: 70–80% работающей функциональности с правильной архитектурой
> (контроллер → сервис → репозиторий, DTO между слоями, без логики в контроллерах).
> Срок-ориентир: ~4–6 часов сфокусированной работы.

---

## 1. Текущее состояние (краткая оценка)

**Архитектура в целом верная**: 4 модуля (`persistence` → `common` → `app`/`rest`),
сервисы в `common`, контроллеры в `app` тонкие, маппинг через MapStruct, security session-based.

**Готово и работает end-to-end** (~65%):
- Регистрация / логин / смена пароля (но email сломан, см. §3.1)
- Просмотр / создание property + загрузка картинок + фильтр на главной
- Избранное (Favorites) + триггер уведомлений
- Чат через STOMP/WebSocket
- Бронирование аренды → оплата → подтверждение
- Срочная продажа (Urgent plan) + оплата
- Админ/менеджер панели, блокировка пользователей
- Liquibase changelogs (1.0 → 2.3) — схема консистентна

**Сломано / не доделано** (что мешает «сдаче»):
- Email конфиг пустой → верификация и сброс пароля не работают
- `Comments`: сервис + БД есть, **нет шаблона**, нет интеграции на странице property
- `Ratings`: сервис + БД есть, **нет ни контроллера, ни шаблона** — фича-сирота
- `BookingController` и `CommentController` имеют **публичные endpoint'ы без auth**
- `BookingController.addBooking` принимает **сырую entity `Booking`** вместо DTO
- Нет глобального `@ControllerAdvice` для бизнес-исключений (только UserControllerAdvice, и тот для атрибутов модели)
- В `propertyDetails.html` не отображается телефон продавца (хотя контроллер кладёт `sellerPhoneMap`)
- Дубль файла `doscker-compos.yml` (опечатка) — мусор
- `Property360` — есть entity (`property_360` с `view_url`), но нет репо/сервиса/UI — **доделать** (см. T12)
- `CurrencyRatesService` — есть вызов exchangerate-api.com, но не используется + нет таблицы (была дропнута в `change-1.6`) — **доделать** (см. T13)
- Логи: typo в `ConversationServiceImpl:54` (`{}` поломано)

---

## 2. Архитектурные правила (на которые опираемся при доделке)

| Слой | Где живёт | Что можно | Что НЕЛЬЗЯ |
|------|-----------|-----------|------------|
| Controller (`app/controller/*`) | приём HTTP, валидация, выбор view | вызывать сервис, класть в `Model`, redirect | дёргать репозитории, делать `@Transactional` логику, маппить entity↔DTO, использовать сырые entity в `@ModelAttribute` |
| Service (`common/service/impl`) | бизнес-логика | `@Transactional`, оркестрация репозиториев, вызов мапперов | возвращать сырые `User`/`Property` наружу для view (только в Model через DTO) |
| Mapper (`common/mapper`) | MapStruct entity↔DTO | — | содержать логику |
| Repository (`persistence/repository`) | доступ к данным | Spring Data методы, спецификации | бизнес-валидация |
| DTO (`persistence/dto`) | передача между слоями + view | — | JPA-аннотации |

**Базовое правило экзамена**: если в контроллере есть `if/for/repository.find...` —
это сигнал переносить в сервис.

---

## 3. План работ по приоритету

### 3.1. КРИТИЧНО (без этого не сдать) — ~30 мин

#### [ ] T1. Починить email
**Проблема:** `application.yaml`: `MAIL_USERNAME=`, `MAIL_PASSWORD=` пусты — спрингбут падает либо
письма не уходят → невозможно зарегистрироваться/сбросить пароль.

**Решение:**
- Создать профиль `application-sam.yaml` (по примеру в репо) с реальными SMTP-кредами Gmail App Password,
  ИЛИ
- На время демо отключить отправку: ввести bean-заглушку `SendMailService` или поле
  `mail.enabled=false` и в `SendEmailServiceImpl` ранний return + лог кода верификации в консоль
  (для экзамена — приемлемо).
- Запускать с `-Dspring-boot.run.profiles=sam`.

#### [ ] T2. Закрыть незащищённые endpoint'ы
**Файл:** `app/config/WebSecurityConfig.java`
- Добавить в `authorizeHttpRequests`:
  - `/bookings/**` → `authenticated()`
  - `/comments/**` → `authenticated()` (а `/comments/delete` лучше внутри сервиса проверять автора)
- В `BookingController.addBooking(...)`: заменить `@ModelAttribute Booking` на DTO
  (создать `BookingCreateDto` в `persistence/dto/booking/` если нет, иначе использовать существующий)
  и делать `bookingService.create(dto, principal)`.

#### [ ] T3. Глобальный `@ControllerAdvice`
**Создать:** `app/controller/GlobalExceptionHandler.java`
- Ловить `BusinessException` / `ResourceNotFoundException` / `AccessDeniedException`
- Возвращать `error.html` (создать в `templates/error.html`) с message
- Логировать stacktrace через `log.error`

---

### 3.2. ВАЖНО (даёт +15% функциональности) — ~1.5 часа

#### [ ] T4. Доделать фичу Comments end-to-end
Сервис и таблица готовы, нужно UI + auth.
- **Шаблон:** интегрировать в `templates/property/propertyDetails.html` секцию:
  - список комментариев (через `th:each` по `comments` из модели)
  - форму добавления (`POST /comments/add` с hidden `propertyId`)
- **Контроллер:** `MainController.propertyDetails(...)` уже грузит property — добавить
  `model.addAttribute("comments", commentService.findAllByPropertyId(id))`.
- **Auth:** `CommentServiceImpl.deleteComment` — проверять, что `comment.user.id == principal.id` или роль ADMIN/MANAGER.
- **Сервис:** убрать создание stub-сущностей `new User(); user.setId(...)` — грузить через `userRepository.getReferenceById(...)`.
- **Удалить** или скрыть GET `/comments` (страница «все комментарии» бессмысленна без модерации). Если оставлять — только для ADMIN.

#### [ ] T5. Создать RatingsController + UI
Сервис `RatingsServiceImpl` полностью готов — нужно только вытащить.
- **Контроллер:** `app/controller/RatingsController.java`
  - `POST /ratings/add` — `@AuthenticationPrincipal`, body: `propertyId`, `value` (1–5) → redirect на propertyDetails
  - (опционально) `GET /ratings/property/{id}` — JSON или фрагмент
- **Шаблон:** в `propertyDetails.html` блок «звёзды»:
  - текущий средний рейтинг (`avg` через сервис)
  - форма «поставить оценку» если пользователь авторизован и не свой объект
- **Security:** добавить `/ratings/**` → `authenticated()`.

#### [ ] T6. Починить отображение в `propertyDetails.html`
- Показать `sellerPhoneMap[property.id]` (контроллер уже кладёт)
- Показать `locationName` вместо `location.id`
- Кнопка «Написать продавцу» — линк на `/messages/open/direct?userId=...&propertyId=...` (роуты уже есть в ChatController)

#### [ ] T7. Привести BookingController в порядок
- Объединить с `PaymentController` flow: оставить `BookingController` только для GET «мои брони» и DELETE
- Удалить публичный `POST /bookings/add` (бронирование уже идёт через `/booking/form` → `/booking/checkout` → `/payment/confirm`)
- Создать шаблон `templates/booking/myBookings.html` с GET `/bookings/my`

---

### 3.3. ШЛИФОВКА (повышает оценку, демонстрирует чистоту) — ~1 час

#### [ ] T8. Конфигурация и dev-удобства
- Удалить дубль `doscker-compos.yml` (опечатка)
- В `application.yaml` дать дефолт для `system.upload.images.directory.path` (например `${user.home}/upload-images`)
- Перенести существующие `app/upload-images/*.png` или добавить в `.gitignore`
- В README/CLAUDE.md добавить кратко «Как запустить» (есть, но проверить что работает)

#### [ ] T9. Чистка кода
- Исправить typo в логе `ConversationServiceImpl:54`
- Убрать неиспользуемые импорты по контроллерам
- Удалить дубль `doscker-compos.yml` (если ещё не сделано в T8)

#### [ ] T10. Тесты (хотя бы базовые, ради экзамена)
- В `common/` уже есть `PropertyServiceImplTest` — убедиться что зелёный
- Добавить 1–2 теста на `FavoritesServiceImpl` (add → remove → list) с `@DataJpaTest` или Mockito
- Добавить 1 интеграционный smoke-тест в `app/` с `@SpringBootTest` + `application-test.yaml` (H2):
  GET `/home` возвращает 200, GET `/loginPage` тоже

#### [ ] T12. Доделать Property360 (виртуальный 3D-тур)
Сейчас есть только entity `Property360 (id, property_id, view_url)`. Не хватает всего остального.

- **Repository:** `persistence/repository/Property360Repository.java`
  - `Optional<Property360> findByPropertyId(int propertyId)`
  - `List<Property360> findAllByPropertyId(int propertyId)` (если допустимо несколько)
- **DTO:** `persistence/dto/property/Property360Dto.java` (`id`, `propertyId`, `viewUrl`)
- **Mapper:** `common/mapper/property/Property360Mapper.java` (MapStruct)
- **Service:** `common/service/Property360Service.java` + `Property360ServiceImpl`
  - `addOrUpdate(int propertyId, String viewUrl, Long currentUserId)` — проверять, что user — владелец property
  - `getByPropertyId(int propertyId)`
  - `delete(int id, Long currentUserId)`
- **Controller:** `app/controller/Property360Controller.java`
  - `POST /property/{id}/view360` — задать/обновить URL (Matterport / Kuula / просто iframe-ссылка)
  - `POST /property/{id}/view360/delete`
- **Шаблоны:**
  - В `createProperty.html` (или отдельная форма «Add 360 view» на странице owner-а property): поле `viewUrl`
  - В `propertyDetails.html`: если `view360 != null` — показывать вкладку «3D-тур» с `<iframe th:src="${view360.viewUrl}" allowfullscreen>` (или ссылку «Открыть 3D-тур»). Контроллер `MainController.propertyDetails` должен класть `view360` в модель.
- **Liquibase:** таблица `property_360` уже создана в `change-1.3-property-details-and-interactions.xml` — проверить колонки, при несоответствии добавить `change-2.4-property-360-fix.xml`.
- **Security:** добавлять/удалять может только владелец property или ADMIN.

#### [ ] T13. Доделать Currency (мультивалютные цены)
Сервис `CurrencyRatesService` ходит на `exchangerate-api.com/v4/latest/USD`, но никем не вызывается. Property хранит цену в одной валюте — нужно дать пользователю выбор отображения.

- **Поднять из мусора:** перенести `CurrencyRatesService` из пакета `service.impl` в `service` (он сам по себе сервис, а не impl) или сделать пару интерфейс + impl как у остальных.
- **Кэширование:** API внешний, не дёргать на каждый рендер главной. Вариант для экзамена:
  - в сервисе поле `Map<String, Double> cachedRates` + `Instant lastFetched`
  - при `getRate(code)`: если `lastFetched < now - 1h` — рефрешим, иначе из кэша
- **Поддерживаемые валюты:** USD, EUR, RUB, AMD (минимум — Армения же).
- **DTO:** уже есть `CurrencyDto` (`code`, `name`, `symbol`, `exchangeRate`) — использовать его как vm.
- **Controller integration:**
  - В `MainController.homePage` и `propertyDetails` принимать query-параметр `?currency=EUR` (по умолчанию USD или валюта из сессии).
  - Класть в модель: `selectedCurrency`, `availableCurrencies`, и для каждой property — конвертированную цену (либо в DTO, либо отдельной map'ой `convertedPriceMap`).
- **Templates:**
  - В `fragments/siteHeader.html` — селектор валюты (dropdown), при смене — GET с `?currency=...`. Сохранять выбор в сессию (`HttpSession.setAttribute("currency", ...)`).
  - В `home.html` и `propertyDetails.html` — рисовать цену + символ выбранной валюты.
- **Безопасность:** `RestTemplate` сейчас — старая практика, но для экзамена ОК. Добавить try/catch + fallback на 1.0 (уже есть).
- **Liquibase:** таблица currency была дропнута в `change-1.6` — НЕ возвращать. Курсы держим в памяти, выбор валюты — в сессии.

#### [ ] T11. Валидация форм
- На DTO-ах создания (`PropertyCreateDto`, `RegisterDto`, `BookingCreateDto`) проверить наличие
  `@NotBlank`, `@Email`, `@Min`, `@Size` — на экзамене любят такое смотреть
- В контроллерах принимать через `@Valid` + `BindingResult`, при ошибках возвращать ту же view с ошибками

---

## 4. Чеклист «готово к сдаче»

Демо-сценарий, который должен пройти без ошибок:

1. [ ] Регистрация нового USER → код в почте/логе → верификация → логин
2. [ ] Логин под админом → блокировка какого-то юзера → этот юзер не может войти
3. [ ] USER создаёт property с 2-3 фото → видно на главной
4. [ ] Фильтр на главной: тип / цена / локация работает
5. [ ] Открыть property → видно фото, цену, телефон, локацию, **средний рейтинг и комментарии**
6. [ ] Поставить рейтинг + написать комментарий → перезагрузка показывает их
7. [ ] Добавить в избранное → переход на `/favorite` показывает property → у владельца property появляется уведомление
8. [ ] Открыть чат с продавцом со страницы property → отправить сообщение → продавец видит в `/messages`
9. [ ] Забронировать аренду → форма оплаты → подтверждение → запись в `/bookings/my`
10. [ ] Срочная продажа: оплатить urgent plan → property поднимается выше в списке
11. [ ] Сброс пароля по email → ввод кода → новый пароль → логин
12. [ ] Владелец property добавляет URL 360-тура → на странице property виден iframe/ссылка на 3D-тур
13. [ ] Переключение валюты USD/EUR/RUB/AMD в шапке → цены на главной и в деталях пересчитываются

Если 11–13 из 13 проходят без stacktrace в браузере → 70–80% достигнуто.

---

## 5. Риски и что НЕ трогать

- **Не трогать `rest` модуль** — у него Java 24 preview, отдельная security, JWT; для экзамена по MVC не нужен.
- **Не править Liquibase прошлые changelog'и** — только новые `change-2.4-*.xml` если потребуется.
- **Не менять структуру модулей** — текущая правильная (persistence → common → app).
- **WebSocket чат не упрощать** — он рабочий, переписывание сломает.
- **MapStruct мапперы** — если падают с null, проверить что `lombok-mapstruct-binding` в `pom.xml` модуля `common`, а не пытаться переписать на ручной маппинг.

---

## 6. Порядок выполнения (рекомендация)

```
День 1 (2-3ч):  T1 → T2 → T3 → T6 → T8
День 2 (3-4ч):  T4 → T5 → T7 → T12 (Property360)
День 3 (2-3ч):  T13 (Currency) → T11 → T9 → T10 → прогон чеклиста §4
```

После каждого пункта — `mvn -pl common test` + ручной запуск `app` и проверка соответствующего сценария из §4.