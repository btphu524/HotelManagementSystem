-- ======================
-- SCHEMA
-- ======================
CREATE SCHEMA IF NOT EXISTS hotel;
SET search_path TO hotel;

-- ======================
-- TABLE: star_ratings
-- ======================
DROP TABLE IF EXISTS star_ratings CASCADE;
CREATE TABLE star_ratings (
    star_rating INT PRIMARY KEY CHECK (star_rating BETWEEN 1 AND 5),
    star_rating_image VARCHAR(100)
);

-- ======================
-- TABLE: hotel_chain
-- ======================
DROP TABLE IF EXISTS hotel_chain CASCADE;
CREATE TABLE hotel_chain (
    hotel_chain_id SERIAL PRIMARY KEY,
    hotel_chain_name VARCHAR(45) NOT NULL,
    hotel_chain_contact_number VARCHAR(20) NOT NULL,
    hotel_chain_email_address VARCHAR(45) UNIQUE NOT NULL,
    hotel_chain_website VARCHAR(100) UNIQUE,
    head_office_address VARCHAR(200) NULL
);

-- ======================
-- TABLE: hotel
-- ======================
DROP TABLE IF EXISTS hotel CASCADE;
CREATE TABLE hotel (
    hotel_id SERIAL PRIMARY KEY,
    hotel_name VARCHAR(45) NOT NULL,
    hotel_contact_number VARCHAR(20) NOT NULL,
    hotel_email_address VARCHAR(45) UNIQUE NOT NULL,
    hotel_website VARCHAR(100) UNIQUE,
    hotel_description VARCHAR(200),
    hotel_floor_count INT CHECK (hotel_floor_count > 0),
    hotel_room_capacity INT CHECK (hotel_room_capacity > 0),
    hotel_chain_id INT REFERENCES hotel_chain(hotel_chain_id) ON DELETE SET NULL,
    address VARCHAR(200) NULL,                     -- <-- thay address_id
    star_rating INT NOT NULL REFERENCES star_ratings(star_rating),
    check_in_time TIME NOT NULL,
    check_out_time TIME NOT NULL,
    CONSTRAINT chk_check_out_after_in CHECK (check_out_time > check_in_time)
);

-- ======================
-- TABLE: room_type
-- ======================
DROP TABLE IF EXISTS room_type CASCADE;
CREATE TABLE room_type (
    room_type_id SERIAL PRIMARY KEY,
    room_type_name VARCHAR(45) NOT NULL,
    room_cost DECIMAL(10,2) NOT NULL CHECK (room_cost >= 0),
    room_type_description VARCHAR(200),
    smoke_friendly BOOLEAN DEFAULT FALSE,
    pet_friendly BOOLEAN DEFAULT FALSE
);

-- ======================
-- TABLE: rooms
-- ======================
DROP TABLE IF EXISTS rooms CASCADE;
CREATE TABLE rooms (
    room_id SERIAL PRIMARY KEY,
    room_number INT NOT NULL,
    room_type_id INT NOT NULL REFERENCES room_type(room_type_id),
    hotel_id INT NOT NULL REFERENCES hotel(hotel_id),
    status VARCHAR(20) NOT NULL DEFAULT 'available' 
        CHECK (status IN ('available', 'occupied', 'maintenance', 'cleaning', 'reserved')),
    UNIQUE(room_number, hotel_id)
);

-- ======================
-- TABLE: guests
-- ======================
DROP TABLE IF EXISTS guests CASCADE;
CREATE TABLE guests (
    guest_id SERIAL PRIMARY KEY,
    guest_first_name VARCHAR(45) NOT NULL,
    guest_last_name VARCHAR(45) NOT NULL,
    guest_contact_number VARCHAR(20) NOT NULL,
    guest_email_address VARCHAR(45) UNIQUE NOT NULL,
    guest_credit_card VARCHAR(19),
    guest_id_proof VARCHAR(100),
    address VARCHAR(200) NULL
);

-- ======================
-- TABLE: department
-- ======================
DROP TABLE IF EXISTS department CASCADE;
CREATE TABLE department (
    department_id SERIAL PRIMARY KEY,
    department_name VARCHAR(45) NOT NULL UNIQUE,
    department_description VARCHAR(200)
);

-- ======================
-- TABLE: employees
-- ======================
DROP TABLE IF EXISTS employees CASCADE;
CREATE TABLE employees (
    emp_id SERIAL PRIMARY KEY,
    emp_first_name VARCHAR(45) NOT NULL,
    emp_last_name VARCHAR(45) NOT NULL,
    emp_designation VARCHAR(45),
    emp_contact_number VARCHAR(20) NOT NULL,
    emp_email_address VARCHAR(45) UNIQUE NOT NULL,
    department_id INT NOT NULL REFERENCES department(department_id),
    address VARCHAR(200) NULL,
    hotel_id INT NOT NULL REFERENCES hotel(hotel_id)
);

-- ============================================================
-- TABLE: accounts (tài khoản nhân viên)
-- ============================================================
DROP TABLE IF EXISTS accounts CASCADE;
CREATE TABLE accounts (
    account_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('admin','manager','receptionist','staff')),
    emp_id INT UNIQUE NOT NULL REFERENCES employees(emp_id) ON DELETE CASCADE
);

-- ============================================================
-- TABLE: accounts_guest (tài khoản khách hàng)
-- ============================================================
DROP TABLE IF EXISTS accounts_guest CASCADE;
CREATE TABLE accounts_guest (
    guest_account_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    guest_id INT UNIQUE NOT NULL REFERENCES guests(guest_id) ON DELETE CASCADE
);

-- ======================
-- TABLE: bookings
-- ======================
DROP TABLE IF EXISTS bookings CASCADE;
CREATE TABLE bookings (
    booking_id SERIAL PRIMARY KEY,
    booking_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    duration_of_stay INT NOT NULL CHECK (duration_of_stay > 0),
    check_in_date TIMESTAMP NOT NULL,
    check_out_date TIMESTAMP NOT NULL,
    booking_payment_type VARCHAR(45) NOT NULL CHECK (booking_payment_type IN ('cash','card','online')),
    total_rooms_booked INT NOT NULL CHECK (total_rooms_booked > 0),
    total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    hotel_id INT NOT NULL REFERENCES hotel(hotel_id),
    guest_id INT NOT NULL REFERENCES guests(guest_id),
    emp_id INT REFERENCES employees(emp_id),
    status VARCHAR(20) NOT NULL DEFAULT 'pending'
        CHECK (status IN ('pending', 'paid', 'confirmed', 'cancelled')),
    CONSTRAINT chk_checkout_after_checkin CHECK (check_out_date > check_in_date)
);

-- ======================
-- TABLE: room_rate_discount
-- ======================
DROP TABLE IF EXISTS room_rate_discount CASCADE;
CREATE TABLE room_rate_discount (
    discount_id SERIAL PRIMARY KEY,
    discount_rate DECIMAL(5,2) NOT NULL CHECK (discount_rate >= 0 AND discount_rate <= 100),
    start_month SMALLINT NOT NULL CHECK (start_month BETWEEN 1 AND 12),
    end_month SMALLINT NOT NULL CHECK (end_month BETWEEN 1 AND 12),
    room_type_id INT NOT NULL REFERENCES room_type(room_type_id),
    CONSTRAINT chk_discount_month CHECK (end_month >= start_month)
);

-- ======================
-- TABLE: rooms_booked
-- ======================
DROP TABLE IF EXISTS rooms_booked CASCADE;
CREATE TABLE rooms_booked (
    rooms_booked_id SERIAL PRIMARY KEY,
    booking_id INT NOT NULL REFERENCES bookings(booking_id) ON DELETE CASCADE,
    room_id INT NOT NULL REFERENCES rooms(room_id) ON DELETE CASCADE,
    UNIQUE(booking_id, room_id)
);

-- ======================
-- TABLE: hotel_services
-- ======================
DROP TABLE IF EXISTS hotel.hotel_services CASCADE;
CREATE TABLE hotel.hotel_services (
    service_id SERIAL PRIMARY KEY,
    service_name VARCHAR(60)  NOT NULL,
    service_description VARCHAR(300),
    service_cost  DECIMAL(10,2) NOT NULL DEFAULT 0.00 CHECK (service_cost >= 0),
    hotel_id INT NULL REFERENCES hotel.hotel(hotel_id) ON DELETE CASCADE,
    is_common BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(service_name, hotel_id)
);

-- ======================
-- TABLE: hotel_services_used_by_guests
-- ======================
DROP TABLE IF EXISTS hotel_services_used_by_guests CASCADE;
CREATE TABLE hotel_services_used_by_guests (
    service_used_id SERIAL PRIMARY KEY,
    service_id INT NOT NULL REFERENCES hotel_services(service_id) ON DELETE CASCADE,
    booking_id INT NOT NULL REFERENCES bookings(booking_id) ON DELETE CASCADE
);

-- ======================
-- INDEXES
-- ======================
CREATE INDEX idx_bookings_hotel_id ON bookings(hotel_id);
CREATE INDEX idx_bookings_guest_id ON bookings(guest_id);
CREATE INDEX idx_rooms_hotel_id ON rooms(hotel_id);
CREATE INDEX idx_rooms_type_id ON rooms(room_type_id);
CREATE INDEX idx_employees_hotel_id ON employees(hotel_id);
CREATE INDEX idx_rooms_booked_booking_id ON rooms_booked(booking_id);
CREATE INDEX idx_rooms_booked_room_id ON rooms_booked(room_id);
CREATE INDEX idx_services_used_booking_id ON hotel_services_used_by_guests(booking_id);
CREATE INDEX idx_services_used_service_id ON hotel_services_used_by_guests(service_id);
CREATE INDEX idx_services_hotel_id ON hotel.hotel_services(hotel_id) WHERE hotel_id IS NOT NULL;
CREATE INDEX idx_services_common   ON hotel.hotel_services(is_common);