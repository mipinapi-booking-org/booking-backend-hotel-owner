package com.github.lukashindy.booking.loader;

import com.github.lukashindy.booking.model.*;
import com.github.lukashindy.booking.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting data loading...");
        
        // Создаем администратора (HotelOwner)
        HotelOwner admin = createHotelOwner();
        
        // Создаем отель
        Hotel hotel = createHotel(admin);
        
        // Создаем типы комнат
        createRoomTypes(hotel);
        
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
    
    private Hotel createHotel(HotelOwner owner) {
        logger.info("Creating hotel...");
        
        Hotel hotel = new Hotel();
        hotel.setName("Grand Paradise Hotel");
        hotel.setCountry("Maldives");
        hotel.setCity("Male");
        hotel.setStreet("Paradise Island Resort, North Male Atoll");
        hotel.setOwner(owner);
        
        Hotel savedHotel = hotelRepository.save(hotel);
        logger.info("Hotel created with ID: {}", savedHotel.getId());
        
        return savedHotel;
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
}
