-- ======================
-- SCHEMA
-- ======================
SET search_path TO hotel;

-- ======================
-- TABLE: star_ratings
-- ======================
INSERT INTO star_ratings(star_rating, star_rating_image) VALUES
 (1,'/images/one_star.jpg'),
 (2,'/images/two_star.jpg'),
 (3,'/images/three_star.jpg'),
 (4,'/images/four_star.jpg'),
 (5,'/images/five_star.jpg');

-- ======================
-- TABLE: department
-- ======================
INSERT INTO department(department_id, department_name, department_description) VALUES
 (1,'Kitchen','cooking'),
 (2,'Cleaning','sweep and mop'),
 (3,'Front Staff','handle bookings and query resolution'),
 (4,'Management','handles customer and resolve complaints'),
 (5,'Commute','pick up and drop');

-- ======================
-- TABLE: room_type
-- ======================
INSERT INTO room_type(room_type_id, room_type_name, room_cost, room_type_description, smoke_friendly, pet_friendly) VALUES
 (1, 'Standard Room',103,'1 King Bed 323-sq-foot room with city views',FALSE,TRUE),
 (2, 'Standard Twin Room',123,'Two Twin Bed 323-sq-foot room with city views',TRUE,TRUE),
 (3, 'Executive Room',130,'1 King Bed 323-sq-foot room with city views',FALSE,FALSE),
 (4, 'Club Room',159,'2 King Bed 323-sq-foot room with city views',TRUE,TRUE);

-- ======================
-- TABLE: room_rate_discount
-- ======================
INSERT INTO room_rate_discount(discount_id, discount_rate, start_month, end_month, room_type_id) VALUES
 (1,50,1,3,1), (2,15,6,8,1), (3,15,9,12,1), (4,0,4,6,1),
 (5,50,1,3,2), (6,80,6,8,2), (7,15,9,12,2), (8,0,4,6,2),
 (9,50,1,3,3), (10,80,6,8,3), (11,15,9,12,3), (12,0,4,6,3);

-- ======================
-- TABLE: hotel_chain
-- ======================
INSERT INTO hotel_chain(hotel_chain_id, hotel_chain_name, hotel_chain_contact_number, hotel_chain_email_address, hotel_chain_website, head_office_address) VALUES
 (1,'Best Western Hotels','0568658956','bw123@gmail.com','https://www.bestwestern.com/','20400 Phoenix, AZ 85027, USA'),
 (2,'China Town Hotels','0105265647','chinatown123@gmail.com','https://www.chinatown.com/','20400 Phoenix, AZ 85027, USA'),
 (3,'Elite Hotels','0468746547','elite.tea213@gmail.com','https://www.elitendhe.com/','8033 King George Boulevard, Surrey, BC V3W 5B4, Canada'),
 (4,'Cosmopolitan Hotels','0527419765','cosmo.hotels123@gmail.com','https://www.cosmopolitan.com/','1565 E South St, Globe, AZ 85501, USA'),
 (5,'Prestige Hotels','0577843647','prestige2453@gmail.com','https://www.prestige.com/','32 Gandhi Road, Mumbai, Maharashtra 534076, India');

-- ======================
-- TABLE: hotel
-- ======================
-- Chain 1: Best Western Hotels
INSERT INTO hotel(hotel_id, hotel_name, hotel_contact_number, hotel_email_address, hotel_website, hotel_description, hotel_floor_count, hotel_room_capacity, hotel_chain_id, address, star_rating, check_in_time, check_out_time) VALUES
 (1,'King George Inn & Suites','0945029564','kgi123@gmail.com','https://www.kgi1.com/','A 2-mile drive from Besh Ba Gowah Archaeological Park.',5,45,1,'8033 King George Boulevard, Surrey, BC V3W 5B4, Canada',4, '08:00:00','16:00:00'),
 (2,'Copper Hills Inn','05479649564','chinni123@gmail.com','https://www.chin12.com/','A 2-mile drive from Besh Ba Gowah Archaeological Park.',6,55,1,'1565 E South St, Globe, AZ 85501, USA', 5, '08:00:00','16:00:00'),
 (3,'Sawmill Inn','0479643452','sawmill.inn@gmail.com','https://www.chin123.com/','A 3-mile drive from Fairview Park.',4,50,1,'32 Gandhi Road, Mumbai, Maharashtra 534076, India', 5,'08:00:00', '16:00:00'),
 (4,'Northgate Inn','0478765422','northgate.inn@gmail.com','https://www.chin1234.com/','A 4-mile drive from Conestoga Mall',3,40,1,'706 Idle rd, Saskatoon, SK S2L 562, Bangladesh', 5,'08:00:00', '16:00:00');

-- Chain 2: China Town Hotels
INSERT INTO hotel.hotel (hotel_id, hotel_name, hotel_contact_number, hotel_email_address, hotel_website, hotel_description, hotel_floor_count, hotel_room_capacity, hotel_chain_id, address, star_rating, check_in_time, check_out_time) VALUES
 (6, 'Chinatown Heritage Hotel Shanghai', '0163228888', 'heritage@chinatown.cn', 'https://www.chinatownheritage.com', 'Historic hotel in Shanghai Old Town', 8, 89, 2, 'No. 123 Nanjing Road, Huangpu District, Shanghai, China', 4, '08:00:00','16:00:00'),
 (7, 'Dragon Gate Inn Beijing', '0165137788', 'beijing@chinatown.cn', 'https://www.dragongateinn.com', 'Near Tiananmen Square', 12, 210, 2, '78 Dongsi West Street, Beijing, China', 5, '08:00:00','16:00:00'),
 (8, 'Golden Phoenix Guangzhou', '0283311888', 'gz@chinatown.cn', 'https://www.goldenphoenix.com', 'Pearl River view luxury hotel', 30, 450, 2, '555 Renmin North Road, Guangzhou, China', 5, '08:00:00','16:00:00');

-- Chain 3: Elite Hotels
INSERT INTO hotel.hotel (hotel_id, hotel_name, hotel_contact_number, hotel_email_address, hotel_website, hotel_description, hotel_floor_count, hotel_room_capacity, hotel_chain_id, address, star_rating, check_in_time, check_out_time) VALUES
 (9, 'Elite Palace Stockholm', '0856621000', 'stockholm@elite.se', 'https://www.elite.se', 'Luxury in central Stockholm', 15, 278, 3, 'Sankt Eriksgatan 115, Stockholm, Sweden', 5, '08:00:00','16:00:00'),
 (10, 'Elite Hotel Marina Tower', '0867055009', 'marina@elite.se', 'https://www.elite.se/marina', 'Stunning waterfront location', 12, 186, 3, 'Saltsjöqvarns Kaj 25, Nacka, Sweden', 5, '08:00:00','16:00:00');

-- Chain 4: Cosmopolitan Hotels
INSERT INTO hotel.hotel (hotel_id, hotel_name, hotel_contact_number, hotel_email_address, hotel_website, hotel_description, hotel_floor_count, hotel_room_capacity, hotel_chain_id, address, star_rating, check_in_time, check_out_time) VALUES
 (11, 'Cosmopolitan Dubai Marina', '0744445555', 'dubai@cosmopolitan.com', 'https://www.cosmopolitan-dubai.com', 'Infinity pool & beach access', 55, 450, 4, 'JBR, Dubai Marina, UAE', 5, '08:00:00','16:00:00'),
 (12, 'Cosmopolitan Singapore', '06563370066', 'singapore@cosmopolitan.com', 'https://www.cosmopolitan.sg', 'Orchard Road luxury', 35, 388, 4, '2 Orchard Turn, Singapore', 5, '08:00:00','16:00:00'),
 (13, 'Cosmopolitan Tokyo Ginza', '0836264580', 'tokyo@cosmopolitan.jp', 'https://www.cosmopolitan-tokyo.com', 'Ginza luxury shopping district', 25, 280, 4, '6-12-3 Ginza, Chuo-ku, Tokyo, Japan', 5, '08:00:00','16:00:00');

-- Chain 5: Prestige Hotels
INSERT INTO hotel.hotel (hotel_id, hotel_name, hotel_contact_number, hotel_email_address, hotel_website, hotel_description, hotel_floor_count, hotel_room_capacity, hotel_chain_id, address, star_rating, check_in_time, check_out_time) VALUES
 (14, 'Prestige Resort Santorini', '0228602500', 'santorini@prestige.gr', 'https://www.prestige-santorini.com', 'Caldera view villas', 6, 72, 5, 'Oia, Santorini, Greece', 5, '08:00:00','16:00:00'),
 (15, 'Prestige Palace Monaco', '0798063636', 'monaco@prestige.mc', 'https://www.prestige-monaco.com', 'Monte Carlo luxury', 10, 95, 5, 'Place du Casino, Monaco', 5, '08:00:00','16:00:00'),
 (16, 'Prestige Maldives Overwater', '0606648888', 'maldives@prestige.com', 'https://www.prestige-maldives.com', 'Private island resort', 1, 60, 5, 'Baa Atoll, Maldives', 5, '08:00:00','16:00:00');

-- ======================
-- TABLE: guests
-- ======================
INSERT INTO guests(guest_id, guest_first_name, guest_last_name, guest_contact_number, guest_email_address, guest_credit_card, guest_id_proof, address) VALUES
 (1,'Jane','Doe','0134568564','jane.doe@gmail.com',NULL,'/images/drivingLicense1023','49 Dave Street, Kitchener, ON N2C 2P6, Canada'),
 (2,'Jerry','Thachter','0568964752','jerry.ytsvg@gmail.com',NULL,'/images/passport45612','64 Victoria Street, Kitchener, ON N2C 2M6, Canada'),
 (3,'Rihanna','Perry','0459867451','rih.vfdj89@gmail.com',NULL,'/images/drivingLicense4889','79 Connaught Street, London, ON N2C 2K3, Canada'),
 (4,'Mathew','Jose','0896248633','mathew.jose@gmail.com',NULL,'/images/drivingLicense8945','45 Sweden St. Street, London, ON N2A 0E4, Canada'),
 (5,'Jessica','Smith','0487958963','jess.smith@gmail.com',NULL,'/images/passport7896','60 Lincoln Street, Guelph, ON N2C 2E8, Canada');

-- ======================
-- TABLE: employees
-- ======================
INSERT INTO employees(emp_id, emp_first_name, emp_last_name, emp_designation, emp_contact_number, emp_email_address, department_id, address, hotel_id) VALUES
 (1,'Jen','Fen','Waiter','0237897896','jen.rds@gmail.com',1,'45 Vanier Park, Kitchener, ON Sd3 d35, Canada',1),
 (2,'Tom','Pitt','Manager','0657897896','tom.pit@gmail.com',3,'41 Greenfield, London, ON 234 987, Canada',1),
 (3,'David','Lawrence','Cashier','0857899896','david.lawr@gmail.com',2,'89 Jacob Rd, Paris, ON 467 289, Canada',1),
 (4,'Joseph','Aniston','Cook','0657847896','joseph.anis@gmail.com',2,'85 Martin Street, Ottawa, BC 263 987, Canada',1),
 (5,'Jeny','Patel','Manager','0178978966','jeny.patel@gmail.com',3,'78 Josseph St. Street, Guelph, BC 267 387, Canada',1);

-- ======================
-- TABLE: rooms
-- ======================
INSERT INTO hotel.rooms (room_number, room_type_id, hotel_id, status) VALUES
-- Hotel 1: King George Inn & Suites (đã có 35, bổ sung thêm cho đẹp)
(1101,1,1,'available'), (1102,1,1,'available'), (1103,2,1,'available'), (1104,2,1,'available'),
(1105,3,1,'available'), (1106,3,1,'available'), (1107,4,1,'available'), (1108,4,1,'available'),

-- Hotel 2: Copper Hills Inn (55 phòng capacity)
(2201,1,2,'available'), (2202,1,2,'available'), (2203,1,2,'available'),
(2204,2,2,'available'), (2205,2,2,'available'), (2206,2,2,'available'),
(2207,3,2,'available'), (2208,3,2,'available'), (2209,4,2,'available'),
(2210,4,2,'available'), (2211,1,2,'available'), (2212,2,2,'available'),

-- Hotel 3: Sawmill Inn
(3301,1,3,'available'), (3302,1,3,'available'), (3303,2,3,'available'), (3304,2,3,'available'),
(3305,3,3,'available'), (3306,4,3,'available'), (3307,1,3,'available'), (3308,2,3,'available'),

-- Hotel 4: Northgate Inn
(4401,1,4,'available'), (4402,2,4,'available'), (4403,3,4,'available'), (4404,4,4,'available'),
(4405,1,4,'available'), (4406,2,4,'available'), (4407,3,4,'available'), (4408,4,4,'available'),

-- Hotel 6: Chinatown Heritage Hotel Shanghai
(6101,1,6,'available'), (6102,2,6,'available'), (6103,3,6,'available'), (6104,4,6,'available'),
(6105,1,6,'available'), (6106,2,6,'available'), (6107,3,6,'available'), (6108,4,6,'available'),
(6109,2,6,'available'), (6110,4,6,'available'),

-- Hotel 7: Dragon Gate Inn Beijing
(7101,4,7,'available'), (7102,4,7,'available'), (7103,3,7,'available'), (7104,3,7,'available'),
(7105,2,7,'available'), (7106,1,7,'available'), (7107,4,7,'available'), (7108,4,7,'available'),

-- Hotel 8: Golden Phoenix Guangzhou
(8101,4,8,'available'), (8102,4,8,'available'), (8103,4,8,'available'), (8104,3,8,'available'),
(8105,3,8,'available'), (8106,2,8,'available'), (8107,1,8,'available'), (8108,4,8,'available'),

-- Hotel 9: Elite Palace Stockholm
(9101,4,9,'available'), (9102,4,9,'available'), (9103,4,9,'available'), (9104,3,9,'available'),
(9105,3,9,'available'), (9106,2,9,'available'), (9107,1,9,'available'), (9108,4,9,'available'),

-- Hotel 10: Elite Hotel Marina Tower
(10101,4,10,'available'), (10102,4,10,'available'), (10103,3,10,'available'), (10104,2,10,'available'),
(10105,1,10,'available'), (10106,4,10,'available'), (10107,4,10,'available'), (10108,3,10,'available'),

-- Hotel 11: Cosmopolitan Dubai Marina
(11101,4,11,'available'), (11102,4,11,'available'), (11103,4,11,'available'), (11104,4,11,'available'),
(11105,4,11,'available'), (11106,3,11,'available'), (11107,3,11,'available'), (11108,2,11,'available'),
(11109,1,11,'available'), (11110,4,11,'available'),

-- Hotel 12: Cosmopolitan Singapore
(12101,4,12,'available'), (12102,4,12,'available'), (12103,4,12,'available'), (12104,3,12,'available'),
(12105,2,12,'available'), (12106,1,12,'available'), (12107,4,12,'available'), (12108,4,12,'available'),

-- Hotel 13: Cosmopolitan Tokyo Ginza
(13101,4,13,'available'), (13102,4,13,'available'), (13103,3,13,'available'), (13104,3,13,'available'),
(13105,2,13,'available'), (13106,1,13,'available'), (13107,4,13,'available'), (13108,4,13,'available'),

-- Hotel 14: Prestige Resort Santorini (cao cấp nhất – toàn Club Room)
(1401,4,14,'available'), (1402,4,14,'available'), (1403,4,14,'available'), (1404,4,14,'available'),
(1405,4,14,'available'), (1406,4,14,'available'), (1407,4,14,'available'), (1408,4,14,'available'),
(1409,4,14,'available'), (1410,4,14,'available'), (1411,4,14,'available'), (1412,4,14,'available'),

-- Hotel 15: Prestige Palace Monaco
(1501,4,15,'available'), (1502,4,15,'available'), (1503,4,15,'available'), (1504,4,15,'available'),
(1505,4,15,'available'), (1506,4,15,'available'), (1507,4,15,'available'), (1508,4,15,'available'),

-- Hotel 16: Prestige Maldives Overwater
(1601,4,16,'available'), (1602,4,16,'available'), (1603,4,16,'available'), (1604,4,16,'available'),
(1605,4,16,'available'), (1606,4,16,'available'), (1607,4,16,'available'), (1608,4,16,'available'),
(1609,4,16,'available'), (1610,4,16,'available');

-- ======================
-- TABLE: hotel_services
-- ======================
INSERT INTO hotel.hotel_services (service_name, service_description, service_cost, hotel_id, is_common) VALUES
-- ================= DỊCH VỤ CHUNG (tất cả khách sạn đều có) =================
('Free High-Speed Wi-Fi', 'Wi-Fi miễn phí toàn khu vực', 0.00, NULL, TRUE),
('24-hour Room Service', 'Phục vụ phòng 24/7', 0.00, NULL, TRUE),
('Laundry & Dry Cleaning', 'Giặt ủi trong ngày', 15.00, NULL, TRUE),
('Currency Exchange', 'Đổi ngoại tệ tại quầy lễ tân', 0.00, NULL, TRUE),
('Concierge Service', 'Hỗ trợ đặt tour, nhà hàng, vé máy bay', 0.00, NULL, TRUE),
('Airport Transfer', 'Đưa đón sân bay (có tính phí)', 60.00, NULL, TRUE),

-- ================= Hotel 1: King George Inn & Suites =================
('Breakfast Buffet', 'Buffet sáng đa dạng món Á-Âu', 25.00, 1, FALSE),
('Business Center', 'Máy in, photocopy, phòng họp nhỏ', 30.00, 1, FALSE),

-- ================= Hotel 2: Copper Hills Inn =================
('Infinity Swimming Pool', 'Hồ bơi vô cực view núi', 120.00, 2, FALSE),
('Gym & Fitness Center', 'Phòng gym 24h đầy đủ máy', 140.00, 2, FALSE),
('Entertainment Room', 'Phòng giải trí riêng (karaoke, PS5)', 80.00, 2, FALSE),
('Spa & Sauna', 'Xông hơi, massage thư giãn', 200.00, 2, FALSE),

-- ================= Hotel 6: Chinatown Heritage Hotel Shanghai =================
('Traditional Tea Ceremony','Trải nghiệm trà đạo Trung Hoa mỗi chiều', 35.00, 6, FALSE),
('Dim Sum Breakfast', 'Bữa sáng dimsum truyền thống', 28.00, 6, FALSE),

-- ================= Hotel 11: Cosmopolitan Dubai Marina =================
('Rooftop Sky Bar', 'Bar trên cao view toàn thành phố', 250.00, 11, FALSE),
('Private Yacht Cruise', 'Du thuyền riêng 2 tiếng', 950.00, 11, FALSE),
('Limousine Service', 'Xe sang đưa đón VIP', 400.00, 11, FALSE),

-- ================= Hotel 14: Prestige Resort Santorini =================
('Private Beach Access', 'Bãi biển riêng chỉ dành cho khách', 300.00, 14, FALSE),
('Luxury Spa & Wellness', 'Spa cao cấp với liệu pháp Hy Lạp', 450.00, 14, FALSE),
('Helicopter Airport Transfer', 'Trực thăng đưa đón từ sân bay', 2500.00, 14, FALSE),
('Sunset Catamaran Cruise', 'Du thuyền ngắm hoàng hôn Santorini', 800.00, 14, FALSE),

-- ================= Hotel 9: Elite Palace Stockholm =================
('Sauna & Ice Bath', 'Trải nghiệm sauna kiểu Bắc Âu', 180.00, 9, FALSE),
('Nordic Breakfast', 'Bữa sáng phong cách Scandinavia', 40.00, 9, FALSE),

-- ================= Hotel 15: Prestige Palace Monaco =================
('Casino Access', 'Vào casino Monte Carlo miễn phí', 0.00, 15, FALSE),
('Yacht Berth Reservation', 'Đặt chỗ neo du thuyền riêng', 1500.00, 15, FALSE);

-- ======================
-- TABLE: bookings
-- ======================
INSERT INTO bookings(booking_id, booking_date, duration_of_stay, check_in_date, check_out_date, booking_payment_type, total_rooms_booked, hotel_id, guest_id, emp_id, total_amount, status) VALUES
 (1,'2018-08-08 00:00:00',5,'2018-08-10 12:00:00','2018-08-15 23:00:00','cash',1,1,1,3,590,'confirmed'),
 (2,'2018-06-08 00:00:00',20,'2018-06-08 12:00:00','2018-06-28 23:00:00','card',1,1,2,1,2300,'confirmed'),
 (3,'2018-06-08 00:00:00',10,'2018-06-08 12:00:00','2018-06-18 23:00:00','card',1,1,1,3,1100,'confirmed'),
 (4,'2018-06-08 00:00:00',2,'2018-06-08 12:00:00','2018-06-10 23:00:00','card',1,1,4,1,290,'confirmed'),
 (5,'2018-06-08 00:00:00',3,'2018-06-08 12:00:00','2018-06-11 23:00:00','card',1,1,2,3,350,'confirmed'),
 (6,'2018-06-08 00:00:00',5,'2018-06-08 12:00:00','2018-06-13 23:00:00','card',1,1,3,3,570,'confirmed'),
 (7,'2018-08-13 00:00:00',2,'2018-06-13 12:00:00','2018-06-15 23:00:00','cash',2,1,5,4,280,'confirmed'),
 (8,'2018-08-10 00:00:00',3,'2018-08-11 12:00:00','2018-08-13 23:00:00','card',1,1,3,3,350,'confirmed'),
 (9,'2018-08-10 00:00:00',5,'2018-08-12 12:00:00','2018-08-16 23:00:00','card',1,1,4,3,570,'confirmed'),
 (10,'2018-08-14 00:00:00',2,'2018-08-15 12:00:00','2018-08-17 23:00:00','cash',2,1,5,4,280,'confirmed'),
 (11,'2018-08-14 00:00:00',5,'2018-08-16 12:00:00','2018-08-21 23:00:00','cash',1,1,1,3,590,'confirmed'),
 (12,'2018-08-14 00:00:00',20,'2018-08-17 12:00:00','2018-09-07 23:00:00','card',1,1,2,1,2300,'confirmed'),
 (13,'2018-08-14 00:00:00',10,'2018-08-15 12:00:00','2018-08-25 23:00:00','card',1,1,1,3,1100,'confirmed'),
 (14,'2018-08-14 00:00:00',2,'2018-08-16 12:00:00','2018-08-18 23:00:00','card',2,1,4,1,290,'confirmed'),
 (15,'2018-08-14 00:00:00',3,'2018-08-17 12:00:00','2018-08-20 23:00:00','card',3,1,2,3,350,'confirmed');

-- ======================
-- TABLE: rooms_booked
-- ======================
INSERT INTO rooms_booked(rooms_booked_id, booking_id, room_id) VALUES
 (1,1,1), (2,2,2), (3,2,3), (4,2,4), (5,2,5), (6,2,6), (7,7,7), (8,7,8),
 (9,6,9), (10,8,10), (11,9,11), (12,10,12), (13,10,13), (14,11,14), (15,12,15),
 (16,13,16), (17,14,17), (18,14,18), (19,15,19), (20,15,20), (21,15,21);

-- ======================
-- TABLE: hotel_services_used_by_guests
-- ======================
INSERT INTO hotel_services_used_by_guests(service_used_id, service_id, booking_id) VALUES
 (1,1,2), (2,2,2), (3,3,2);

-- ======================
-- TABLE: accounts_guests
-- ======================
 INSERT INTO hotel.accounts_guest (username, password_hash, guest_id) VALUES
 ('jane.doe@gmail.com', '$2a$10$CIIbjWZe46QuF3ILPWciduRa7Q2mjnuQJkqEbdOLE5liVwP11tNoy', 1),
 ('jerry.ytsvg@gmail.com', '$2a$10$CIIbjWZe46QuF3ILPWciduRa7Q2mjnuQJkqEbdOLE5liVwP11tNoy', 2),
 ('rih.vfdj89@gmail.com', '$2a$10$CIIbjWZe46QuF3ILPWciduRa7Q2mjnuQJkqEbdOLE5liVwP11tNoy', 3),
 ('mathew.jose@gmail.com', '$2a$10$CIIbjWZe46QuF3ILPWciduRa7Q2mjnuQJkqEbdOLE5liVwP11tNoy', 4),
 ('jessica@gmail.com', '$2a$10$CIIbjWZe46QuF3ILPWciduRa7Q2mjnuQJkqEbdOLE5liVwP11tNoy', 5);

-- ======================
-- TABLE: accounts
-- ======================
 INSERT INTO hotel.accounts (username, password_hash, role, emp_id) VALUES
 ('jen.rds@gmail.com', '$2a$10$CIIbjWZe46QuF3ILPWciduRa7Q2mjnuQJkqEbdOLE5liVwP11tNoy', 'staff', 1),  -- Jen Fen - Waiter
 ('tom.pit@gmail.com', '$2a$10$CIIbjWZe46QuF3ILPWciduRa7Q2mjnuQJkqEbdOLE5liVwP11tNoy', 'manager', 2),  -- Tom Pitt - Manager
 ('david.lawr@gmail.com', '$2a$10$CIIbjWZe46QuF3ILPWciduRa7Q2mjnuQJkqEbdOLE5liVwP11tNoy', 'receptionist', 3),  -- David Lawrence - Cashier
 ('joseph.anis@gmail.com', '$2a$10$CIIbjWZe46QuF3ILPWciduRa7Q2mjnuQJkqEbdOLE5liVwP11tNoy', 'staff', 4),  -- Joseph Aniston - Cook
 ('jeny.patel@gmail.com', '$2a$10$CIIbjWZe46QuF3ILPWciduRa7Q2mjnuQJkqEbdOLE5liVwP11tNoy', 'manager', 5);  -- Jeny Patel - Manager