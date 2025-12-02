-- 1. guests
SELECT setval('hotel.guests_guest_id_seq', COALESCE((SELECT MAX(guest_id) FROM hotel.guests), 1));

-- 2. bookings
SELECT setval('hotel.bookings_booking_id_seq', COALESCE((SELECT MAX(booking_id) FROM hotel.bookings), 1));

-- 3. rooms_booked
SELECT setval('hotel.rooms_booked_rooms_booked_id_seq', COALESCE((SELECT MAX(rooms_booked_id) FROM hotel.rooms_booked), 1));

-- 4. hotel_services_used_by_guests
SELECT setval('hotel.hotel_services_used_by_guests_service_used_id_seq', COALESCE((SELECT MAX(service_used_id) FROM hotel.hotel_services_used_by_guests), 1));

-- 5. accounts_guest (sắp tới sẽ dùng khi khách đăng ký tài khoản)
SELECT setval('hotel.accounts_guest_guest_account_id_seq', COALESCE((SELECT MAX(guest_account_id) FROM hotel.accounts_guest), 1));
