# Booking Management API - Руководство по использованию

## Обзор функционала
Система управления бронированием для персонала отеля с поддержкой всех стадий жизненного цикла заказа.

## Жизненный цикл статусов бронирования

```
CREATED → CONFIRMED → COMPLETED
   ↓           ↓
REFUSED    CANCELLED
```

## API Endpoints

### 1. Получить все бронирования
```bash
GET /api/bookings
```

### 2. Получить бронирование по ID
```bash
GET /api/bookings/{id}
```

### 3. Получить бронирования по статусу
```bash
GET /api/bookings/status/{status}
```
Возможные статусы: `CREATED`, `CONFIRMED`, `REFUSED`, `CANCELLED`, `COMPLETED`

### 4. Подтвердить бронирование
```bash
PATCH /api/bookings/{id}/confirm?hotelOwnerId={uuid}
```
- Переводит статус из `CREATED` в `CONFIRMED`
- Устанавливает `updatedBy` и `lastUpdatedDate`
- Очищает `refuseReason` если был установлен

### 5. Отклонить бронирование
```bash
PATCH /api/bookings/{id}/refuse?hotelOwnerId={uuid}
Content-Type: application/json

{
  "reason": "Причина отказа обязательна"
}
```
- Переводит статус из `CREATED` в `REFUSED`
- Обязательно указание причины отказа
- Устанавливает `refuseReason`, `updatedBy` и `lastUpdatedDate`

### 6. Завершить бронирование
```bash
PATCH /api/bookings/{id}/complete?hotelOwnerId={uuid}
```
- Переводит статус из `CONFIRMED` в `COMPLETED`
- Только для подтвержденных бронирований
- Устанавливает `updatedBy` и `lastUpdatedDate`

### 7. Отменить бронирование
```bash
PATCH /api/bookings/{id}/cancel?hotelOwnerId={uuid}
```
- Переводит статус в `CANCELLED`
- Устанавливает `updatedBy` и `lastUpdatedDate`

## Примеры использования

### Подтверждение бронирования
```bash
curl -X PATCH "http://localhost:8080/api/bookings/1/confirm?hotelOwnerId=b209ceed-2670-4573-97d8-5ad410a08756"
```

### Отклонение с указанием причины
```bash
curl -X PATCH "http://localhost:8080/api/bookings/2/refuse?hotelOwnerId=b209ceed-2670-4573-97d8-5ad410a08756" \
  -H "Content-Type: application/json" \
  -d '{"reason": "Номер временно недоступен для бронирования"}'
```

### Завершение бронирования
```bash
curl -X PATCH "http://localhost:8080/api/bookings/1/complete?hotelOwnerId=b209ceed-2670-4573-97d8-5ad410a08756"
```

## Особенности системы

### Валидация переходов статусов
- Из `CREATED` можно перейти только в `CONFIRMED`, `REFUSED` или `CANCELLED`
- Из `CONFIRMED` можно перейти только в `COMPLETED` или `CANCELLED`
- Завершенные статусы (`REFUSED`, `CANCELLED`, `COMPLETED`) нельзя изменить

### Обязательные поля при операциях
- **При отклонении**: обязательно указание причины в поле `reason`
- **При всех операциях**: обязательно указание `hotelOwnerId`

### Автоматические поля
- `updatedBy` - автоматически устанавливается при любом изменении статуса
- `lastUpdatedDate` - автоматически устанавливается при любом изменении статуса
- `refuseReason` - очищается при подтверждении бронирования

## Тестовые данные
При запуске приложения автоматически создаются:
- 46 комнат разных типов (Standard, Deluxe, Presidential и др.)
- 8 тестовых бронирований с непересекающимися датами
- Разнообразные статусы для демонстрации функционала
- ID администратора можно получить из логов при запуске

## Структура ответа
```json
{
  "id": 1,
  "roomId": 1,
  "clientId": "fa14fb56-7370-4d16-a0d1-c10a5f13bd36",
  "checkInDate": "2025-07-28T14:00:00",
  "checkOutDate": "2025-07-30T14:00:00",
  "guestFullNames": "Иван Петров, Алексей Сидоров",
  "specialRequests": "Требуется детская кроватка",
  "status": "CONFIRMED",
  "refuseReason": null,
  "updatedBy": "b209ceed-2670-4573-97d8-5ad410a08756",
  "lastUpdatedDate": "2025-07-27T21:48:47.290339"
}
```
