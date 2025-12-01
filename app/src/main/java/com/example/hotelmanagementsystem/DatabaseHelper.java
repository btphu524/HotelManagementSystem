package com.example.hotelmanagementsystem;

import static java.sql.DriverManager.getConnection;

import android.util.Log;

import com.example.hotelmanagementsystem.form2.GuestLoginResult;
import com.example.hotelmanagementsystem.form2.Hotel;
import com.example.hotelmanagementsystem.form2.HotelService;
import com.example.hotelmanagementsystem.form2.Room;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;
public class DatabaseHelper {
    private static final Logger LOGGER = Logger.getLogger(DatabaseHelper.class.getName());
    public static final String DB_URL = "jdbc:postgresql://192.168.0.103:5432/Hotel_Management_DB";
    public static final String DB_USER = "postgres";
    public static final String DB_PASS = "123456";

    private Connection connect() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL Driver not found", e);
        }
    }

    /**
     * Tạo khách hàng mới
     * @return guest_id nếu thành công, -1 nếu thất bại (email trùng, lỗi mạng, v.v.)
     */
    public int createGuest(String firstName, String lastName, String phone, String email,
                           String creditCard, String idProofUrl, String fullAddress) {

        // Kiểm tra bắt buộc trước khi kết nối DB (tiết kiệm thời gian)
        if (firstName == null || firstName.trim().isEmpty() ||
                lastName == null || lastName.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                idProofUrl == null || idProofUrl.trim().isEmpty()) {
            return -1;
        }

        String sql = "INSERT INTO hotel.guests " +
                "(guest_first_name, guest_last_name, guest_contact_number, guest_email_address, " +
                "guest_credit_card, guest_id_proof, address) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, firstName.trim());
            pst.setString(2, lastName.trim());
            pst.setString(3, phone.trim());
            pst.setString(4, email.trim().toLowerCase()); // email lowercase để tránh trùng

            // Credit card: nếu rỗng → NULL (đúng cách)
            if (creditCard == null || creditCard.trim().isEmpty()) {
                pst.setNull(5, Types.VARCHAR);
            } else {
                pst.setString(5, creditCard.trim());
            }

            pst.setString(6, idProofUrl.trim());

            // Address: nếu rỗng → NULL
            if (fullAddress == null || fullAddress.trim().isEmpty()) {
                pst.setNull(7, Types.VARCHAR);
            } else {
                pst.setString(7, fullAddress.trim());
            }

            pst.executeUpdate();  // ← INSERT bình thường

            // Lấy guest_id vừa sinh ra bằng currval (bỏ qua cache hoàn toàn)
            String sqlCurrval = "SELECT currval('hotel.guests_guest_id_seq')";
            try (PreparedStatement pstCurrval = conn.prepareStatement(sqlCurrval);
                 ResultSet rs = pstCurrval.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);  // Trả về ID đúng 100%
                }
            }

        } catch (SQLException e) {
            // Thông báo lỗi cụ thể để debug
            System.err.println("Lỗi tạo khách hàng:");
            System.err.println("Message: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            if ("23505".equals(e.getSQLState())) {
                System.err.println("→ Email đã được sử dụng!");
            }
            e.printStackTrace();
        }
        return -1; // Thất bại
    }

    public static GuestLoginResult loginAndGetGuestInfo(String username, String password) {
        String sql = "SELECT ag.guest_id, " +
                "g.guest_first_name, " +
                "g.guest_last_name, " +
                "g.guest_email_address AS email, " +
                "g.guest_contact_number AS phone_number, " +
                "ag.password_hash " +
                "FROM hotel.accounts_guest ag " +
                "JOIN hotel.guests g ON ag.guest_id = g.guest_id " +
                "WHERE ag.username ILIKE ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashed = rs.getString("password_hash");
                if (BCrypt.checkpw(password, hashed)) {
                    return new GuestLoginResult(
                            rs.getInt("guest_id"),
                            rs.getString("guest_first_name"),
                            rs.getString("guest_last_name"),
                            rs.getString("email"),
                            rs.getString("phone_number")
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Login error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tạo booking mới – trả về booking_id nếu thành công, -1 nếu thất bại
     */
    public static long createBooking(
            int guestId,
            int hotelId,
            LocalDate checkIn,
            LocalDate checkOut,
            List<Integer> selectedRoomIds,
            List<Integer> selectedServiceIds,
            String paymentType) {

        if (selectedRoomIds == null || selectedRoomIds.isEmpty() || checkIn.isAfter(checkOut) || checkIn.isBefore(LocalDate.now())) {
            return -1;
        }

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return -1;
        }

        String insertBookingSQL = "INSERT INTO hotel.bookings (\n" +
                "    booking_date, duration_of_stay, check_in_date, check_out_date,\n" +
                "    booking_payment_type, total_rooms_booked, total_amount,\n" +
                "    hotel_id, guest_id, status\n" +
                ") VALUES (\n" +
                "    NOW(), ?, ?, ?,\n" +
                "    ?, ?, ?,\n" +
                "    ?, ?, 'pending'\n" +
                ") RETURNING booking_id";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false);

            BigDecimal totalAmount = calculateTotalAmount(conn, selectedRoomIds, selectedServiceIds, checkIn, hotelId);

            long bookingId;
            try (PreparedStatement ps = conn.prepareStatement(insertBookingSQL)) {
                long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
                ps.setLong(1, nights);
                ps.setDate(2, java.sql.Date.valueOf(checkIn.toString()));                    // check_in_date
                ps.setDate(3, java.sql.Date.valueOf(checkOut.toString()));                   // check_out_date (ngày checkout khách rời đi)
                ps.setString(4, paymentType);
                ps.setInt(5, selectedRoomIds.size());
                ps.setBigDecimal(6, totalAmount);
                ps.setInt(7, hotelId);
                ps.setInt(8, guestId);

                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    return -1;
                }
                bookingId = rs.getLong(1);
            }

            // rooms_booked
            String sqlRoom = "INSERT INTO hotel.rooms_booked (booking_id, room_id) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlRoom)) {
                for (int roomId : selectedRoomIds) {
                    ps.setLong(1, bookingId);
                    ps.setInt(2, roomId);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // services
            if (selectedServiceIds != null && !selectedServiceIds.isEmpty()) {
                String sqlService = "INSERT INTO hotel.hotel_services_used_by_guests (service_id, booking_id) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlService)) {
                    for (int sid : selectedServiceIds) {
                        ps.setInt(1, sid);
                        ps.setLong(2, bookingId);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            // update room status
            String sqlUpdate = "UPDATE hotel.rooms SET status = 'reserved' WHERE room_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                for (int roomId : selectedRoomIds) {
                    ps.setInt(1, roomId);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return bookingId;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // 1. calculateTotalAmount – đã sửa cho Java 11
    public static BigDecimal calculateTotalAmount(Connection conn,
                                                   List<Integer> roomIds,
                                                   List<Integer> serviceIds,
                                                   LocalDate checkIn,
                                                   int hotelId) throws SQLException {

        BigDecimal total = BigDecimal.ZERO;

        String roomSQL = "SELECT rt.room_cost, COALESCE(d.discount_rate, 0) AS discount_rate " +
                "FROM hotel.rooms r " +
                "JOIN hotel.room_type rt ON r.room_type_id = rt.room_type_id " +
                "LEFT JOIN hotel.room_rate_discount d ON rt.room_type_id = d.room_type_id " +
                "  AND EXTRACT(MONTH FROM ?::date) BETWEEN d.start_month AND d.end_month " +
                "WHERE r.room_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(roomSQL)) {
            for (int roomId : roomIds) {
                ps.setDate(1, java.sql.Date.valueOf(checkIn.toString()));
                ps.setInt(2, roomId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        BigDecimal cost = rs.getBigDecimal("room_cost");
                        double discount = rs.getDouble("discount_rate");
                        BigDecimal priceAfterDiscount = cost.multiply(BigDecimal.valueOf(100 - discount))
                                .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
                        total = total.add(priceAfterDiscount);
                    }
                }
            }
        }

        if (serviceIds != null && !serviceIds.isEmpty()) {
            String svcSQL = "SELECT service_cost FROM hotel.hotel_services WHERE service_id = ? AND hotel_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(svcSQL)) {
                for (int sid : serviceIds) {
                    ps.setInt(1, sid);
                    ps.setInt(2, hotelId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            total = total.add(rs.getBigDecimal("service_cost"));
                        }
                    }
                }
            }
        }

        return total;
    }

    // 2. getAvailableRooms – đã sửa cho Java 11
    public static List<Room> getAvailableRooms(int hotelId, LocalDate checkIn, LocalDate checkOut) {
        List<Room> list = new ArrayList<>();

        String sql = "SELECT DISTINCT r.room_id, r.room_number, rt.room_type_name, rt.room_cost " +
                "FROM hotel.rooms r " +
                "JOIN hotel.room_type rt ON r.room_type_id = rt.room_type_id " +
                "WHERE r.hotel_id = ? " +
                "  AND r.status = 'available' " +
                "  AND r.room_id NOT IN ( " +
                "    SELECT rb.room_id " +
                "    FROM hotel.rooms_booked rb " +
                "    JOIN hotel.bookings b ON rb.booking_id = b.booking_id " +
                "    WHERE b.status NOT IN ('cancelled', 'checked_out') " +
                "      AND b.check_in_date::date < ? " +
                "      AND b.check_out_date::date > ? " +
                "  ) " +
                "ORDER BY r.room_number";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, hotelId);
            ps.setObject(2, checkOut);     // check_out_date > checkIn bạn chọn
            ps.setObject(3, checkIn);      // check_in_date < checkOut bạn chọn

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Room(
                            rs.getInt("room_id"),
                            rs.getString("room_number"),
                            rs.getString("room_type_name"),
                            rs.getBigDecimal("room_cost"),
                            0.0
                    ));
                }
            }
        } catch (Exception e) {
            Log.e("DB", "Lỗi lấy phòng trống", e);
        }
        return list;
    }

    // 3. getServicesByHotel – đã sửa cho Java 11
    public static List<HotelService> getServicesByHotel(int hotelId) {
        List<HotelService> list = new ArrayList<>();
        String sql = "SELECT service_id, service_name, service_cost " +
                "FROM hotel.hotel_services " +
                "WHERE hotel_id = ? " +
                "ORDER BY service_name";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hotelId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new HotelService(
                            rs.getInt("service_id"),
                            rs.getString("service_name"),
                            rs.getBigDecimal("service_cost")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // === THÊM VÀO DatabaseHelper.java ===
// Hàm lấy danh sách khách sạn theo chuỗi (chain)
    public static List<Hotel> getHotelsByChain(int chainId) {
        List<Hotel> list = new ArrayList<>();
        String sql = "SELECT h.hotel_id, h.hotel_name, h.star_rating " +
                "FROM hotel.hotel h " +
                "WHERE h.hotel_chain_id = ? " +
                "ORDER BY h.hotel_name";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, chainId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Hotel(
                            rs.getInt("hotel_id"),
                            rs.getString("hotel_name"),
                            chainId,
                            getChainName(chainId), // <-- hàm này đã sửa dưới đây
                            rs.getInt("star_rating")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // === HÀM getChainName() – ĐÃ SỬA CHO JAVA 11 (không dùng switch expression) ===
    private static String getChainName(int chainId) {
        if (chainId == 1) return "Best Western";
        if (chainId == 2) return "China Town";
        if (chainId == 3) return "Elite";
        if (chainId == 4) return "Cosmopolitan";
        if (chainId == 5) return "Prestige";
        return "Unknown Chain";
    }
}