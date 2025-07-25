# Booking Backend Hotel Owner

Это Spring Boot 3 приложение для управления отелями в системе бронирования.

## Параметры проекта

- **Group ID**: `com.github.lukashindy`
- **Artifact ID**: `booking-backend-hotel-owner`
- **Version**: `0.0.1-SNAPSHOT`
- **Java Version**: 17
- **Spring Boot Version**: 3.2.0

## Структура проекта

```
src/
├── main/
│   ├── java/
│   │   └── com/github/lukashindy/booking/
│   │       ├── BookingBackendHotelOwnerApplication.java  # Главный класс приложения
│   │       └── controller/
│   │           └── HotelOwnerController.java             # REST контроллер
│   └── resources/
│       └── application.properties                        # Конфигурация приложения
└── test/
    └── java/
        └── com/github/lukashindy/booking/controller/
            └── HotelOwnerControllerTest.java              # Тесты контроллера
```

## Доступные REST endpoints

- `GET /api/hotel-owner/hello` - Возвращает приветственное сообщение
- `GET /api/hotel-owner/status` - Возвращает статус сервиса

## Как запустить

### Предварительные требования

- Java 17 или выше
- Maven 3.6 или выше

### Запуск приложения

1. Клонируйте репозиторий
2. Перейдите в директорию проекта
3. Запустите команду:
   ```bash
   mvn spring-boot:run
   ```

Приложение будет доступно по адресу: `http://localhost:8080`

### Запуск тестов

```bash
mvn test
```

### Сборка проекта

```bash
mvn clean package
```

## Тестирование API

Вы можете протестировать API используя curl:

```bash
# Проверка hello endpoint
curl http://localhost:8080/api/hotel-owner/hello

# Проверка status endpoint  
curl http://localhost:8080/api/hotel-owner/status
```

## Конфигурация

Основные настройки находятся в файле `src/main/resources/application.properties`:

- `server.port=8080` - порт приложения
- `spring.application.name=booking-backend-hotel-owner` - имя приложения
- Настройки логирования
