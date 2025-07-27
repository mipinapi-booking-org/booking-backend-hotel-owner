package com.github.lukashindy.booking.loader;

import com.github.lukashindy.booking.model.*;
import com.github.lukashindy.booking.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class DataLoader implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);
    
    @Autowired
    private HotelOwnerRepository hotelOwnerRepository;
    
    @Autowired
    private HotelRepository hotelRepository;
    
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private HotelOwnerAccessRepository hotelOwnerAccessRepository;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Starting data loading...");
        
        // Создаем администратора (HotelOwner)
        HotelOwner admin = createHotelOwner();
        
        // Создаем отель
        Hotel hotel = createHotel();
        
        // Создаем связь доступа между админом и отелем
        createHotelAccess(admin, hotel);
        
        // Создаем типы комнат
        createRoomTypes(hotel);
        
        // Создаем тестовые бронирования
        createTestBookings(admin);
        
        logger.info("Data loading completed successfully!");
    }
    
    private HotelOwner createHotelOwner() {
        logger.info("Creating hotel owner...");
        
        HotelOwner owner = new HotelOwner();
        owner.setName("John Smith");
        owner.setEmail("admin@grandhotel.com");
        owner.setPasswordHash("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"); // password: secret
        owner.setRole("ADMIN");
        
        HotelOwner savedOwner = hotelOwnerRepository.save(owner);
        logger.info("Hotel owner created with ID: {}", savedOwner.getId());
        
        return savedOwner;
    }
    
    private Hotel createHotel() {
        logger.info("Creating hotel...");
        
        Hotel hotel = new Hotel();
        hotel.setName("Grand Paradise Hotel");
        hotel.setCountry("Maldives");
        hotel.setCity("Male");
        hotel.setStreet("Paradise Island Resort, North Male Atoll");
        
        Hotel savedHotel = hotelRepository.save(hotel);
        logger.info("Hotel created with ID: {}", savedHotel.getId());
        
        return savedHotel;
    }
    
    private void createHotelAccess(HotelOwner owner, Hotel hotel) {
        logger.info("Creating hotel access for owner {} to hotel {}", owner.getId(), hotel.getId());
        
        HotelOwnerAccess access = new HotelOwnerAccess();
        access.setHotelOwner(owner);
        access.setHotel(hotel);
        access.setGrantedAt(LocalDateTime.now());
        access.setGrantedBy(owner.getId()); // Владелец сам себе дает права
        access.setAccessLevel(HotelOwnerAccess.AccessLevel.OWNER);
        
        hotelOwnerAccessRepository.save(access);
        logger.info("Hotel access created successfully");
    }
    
    private void createRoomTypes(Hotel hotel) {
        logger.info("Creating room types...");
        
        // Массив типов комнат с их характеристиками
        String[][] roomTypeData = {
            {"Standard", "2"},
            {"Standard Sea View", "2"},
            {"Deluxe", "3"},
            {"Comfort", "2"},
            {"King Size", "2"},
            {"Presidential", "4"},
            {"Economic", "1"}
        };
        
        for (String[] data : roomTypeData) {
            String typeName = data[0];
            int capacity = Integer.parseInt(data[1]);
            
            logger.info("Creating room type: {}", typeName);
            
            // Создаем тип комнаты
            RoomType roomType = new RoomType();
            roomType.setName(typeName);
            roomType.setCapacity(capacity);
            roomType.setHotel(hotel);
            
            RoomType savedRoomType = roomTypeRepository.save(roomType);
            logger.info("Room type '{}' created with ID: {}", typeName, savedRoomType.getId());
            
            // Создаем комнаты для каждого типа
            createRoomsForType(savedRoomType, hotel);
        }
    }
    
    private void createRoomsForType(RoomType roomType, Hotel hotel) {
        String typeName = roomType.getName();
        int roomCount = getRoomCount(typeName);
        
        logger.info("Creating {} rooms for type '{}'", roomCount, typeName);
        
        for (int i = 1; i <= roomCount; i++) {
            Room room = new Room();
            room.setRoomNumber(generateRoomNumber(typeName, i));
            room.setRoomType(roomType);
            room.setHotel(hotel);
            
            Room savedRoom = roomRepository.save(room);
            logger.debug("Room {} created with ID: {}", savedRoom.getRoomNumber(), savedRoom.getId());
        }
        
        logger.info("Created {} rooms for type '{}'", roomCount, typeName);
    }
    
    private int getRoomCount(String typeName) {
        // Определяем количество комнат для каждого типа
        switch (typeName) {
            case "Standard": return 10;
            case "Standard Sea View": return 8;
            case "Deluxe": return 6;
            case "Comfort": return 7;
            case "King Size": return 5;
            case "Presidential": return 2;
            case "Economic": return 8;
            default: return 5;
        }
    }
    
    private String generateRoomNumber(String typeName, int roomIndex) {
        // Генерируем номер комнаты на основе типа
        String prefix;
        switch (typeName) {
            case "Standard": prefix = "ST"; break;
            case "Standard Sea View": prefix = "SV"; break;
            case "Deluxe": prefix = "DX"; break;
            case "Comfort": prefix = "CF"; break;
            case "King Size": prefix = "KS"; break;
            case "Presidential": prefix = "PR"; break;
            case "Economic": prefix = "EC"; break;
            default: prefix = "RM"; break;
        }
        
        return String.format("%s-%03d", prefix, roomIndex);
    }
    
    private void createTestBookings(HotelOwner admin) {
        logger.info("Creating test bookings...");
        
        // Получаем список всех комнат для создания бронирований
        List<Room> allRooms = roomRepository.findAll();
        
        if (allRooms.isEmpty()) {
            logger.warn("No rooms found for creating bookings");
            return;
        }
        
        // Создаем тестовые бронирования с непересекающимися датами
        LocalDateTime baseDate = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
        
        // Данные для тестовых бронирований
        String[][] bookingData = {
            {"Иван Петров", "Алексей Сидоров", "Требуется детская кроватка"},
            {"Maria Garcia", "John Smith", "Безглютеновое питание"},
            {"Анна Козлова", "Михаил Иванов", "Поздний заезд после 22:00"},
            {"Pierre Dubois", "Marie Laurent", "Номер на высоком этаже"},
            {"李明", "王丽", "Халяльное питание"},
            {"Ahmed Hassan", "", "Тихий номер"},
            {"Elena Rossi", "Giuseppe Bianchi", "Романтический ужин"},
            {"Hans Mueller", "", "Трансфер из аэропорта"}
        };
        
        int bookingIndex = 0;
        for (int roomIndex = 0; roomIndex < Math.min(allRooms.size(), 15) && bookingIndex < bookingData.length; roomIndex++) {
            Room room = allRooms.get(roomIndex);
            
            // Создаем несколько бронирований для одной комнаты с разными периодами
            int bookingsPerRoom = (roomIndex % 3) + 1; // От 1 до 3 бронирований на комнату
            
            LocalDateTime roomBookingStart = baseDate.plusDays(roomIndex * 7L); // Каждая комната начинает с недельным сдвигом
            
            for (int i = 0; i < bookingsPerRoom && bookingIndex < bookingData.length; i++) {
                String[] guestData = bookingData[bookingIndex];
                
                // Вычисляем даты для каждого бронирования в комнате (непересекающиеся)
                LocalDateTime checkIn = roomBookingStart.plusDays(i * 4L); // 4 дня между бронированиями
                LocalDateTime checkOut = checkIn.plusDays(2 + (i % 2)); // 2-3 дня проживания
                
                // Создаем бронирование
                Booking booking = new Booking();
                booking.setRoom(room);
                booking.setClientId(UUID.randomUUID());
                booking.setCheckInDate(checkIn);
                booking.setCheckOutDate(checkOut);
                booking.setGuestFullNames(guestData[0] + (!guestData[1].isEmpty() ? ", " + guestData[1] : ""));
                booking.setSpecialRequests(guestData.length > 2 ? guestData[2] : null);
                booking.setStatus(getRandomInitialStatus(bookingIndex));
                
                // Для некоторых бронирований добавляем информацию об обновлении
                if (booking.getStatus() != Booking.Status.CREATED) {
                    booking.setUpdatedBy(admin);
                    booking.setLastUpdatedDate(checkIn.minusDays(3 + (bookingIndex % 5)));
                    
                    // Добавляем причину отказа для отклоненных бронирований
                    if (booking.getStatus() == Booking.Status.REFUSED) {
                        String[] refuseReasons = {
                            "Номер временно недоступен для бронирования",
                            "Превышена максимальная вместимость номера",
                            "Запрошенные даты пересекаются с техническими работами",
                            "Специальные требования не могут быть выполнены"
                        };
                        booking.setRefuseReason(refuseReasons[bookingIndex % refuseReasons.length]);
                    }
                }
                
                bookingRepository.save(booking);
                
                logger.debug("Created booking for room {} from {} to {}, status: {}", 
                    room.getRoomNumber(), 
                    checkIn.toLocalDate(), checkOut.toLocalDate(), booking.getStatus());
                
                bookingIndex++;
            }
        }
        
        logger.info("Created {} test bookings with non-overlapping dates", bookingIndex);
    }
    
    private Booking.Status getRandomInitialStatus(int index) {
        // Распределяем статусы для разнообразия тестовых данных
        Booking.Status[] statuses = {
            Booking.Status.CREATED,     // 40%
            Booking.Status.CREATED,
            Booking.Status.CONFIRMED,   // 25%
            Booking.Status.REFUSED,     // 20%
            Booking.Status.COMPLETED,   // 10%
            Booking.Status.CANCELLED    // 5%
        };
        
        return statuses[index % statuses.length];
    }
}
