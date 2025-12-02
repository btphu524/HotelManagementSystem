package com.example.hotelmanagementsystem.form2;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelmanagementsystem.DatabaseHelper;
import com.example.hotelmanagementsystem.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CreateBookingActivity extends AppCompatActivity {

    private static final String TAG = "CreateBooking";

    private TextInputEditText etCheckIn, etCheckOut;
    private TextView tvNights, tvTotalAmount;
    private MaterialButton btnConfirmBooking;
    private RadioGroup rgPaymentType;
    private Spinner spinnerHotelChain, spinnerHotels;
    private LinearLayout containerRooms, containerServices;
    private ScrollView scrollRooms, scrollServices;

    private LocalDate checkInDate, checkOutDate;
    private int selectedHotelId = -1;
    private int guestId = -1;
    private long nights = 1;
    private TextInputEditText etCheckInTime, etCheckOutTime;
    private int checkInHour = 14, checkInMinute = 0;   // mặc định 14:00
    private int checkOutHour = 12, checkOutMinute = 0; // mặc định 12:00

    private final List<Integer> selectedRoomIds = new ArrayList<>();
    private final List<Integer> selectedServiceIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_booking);

        initViews();
        getGuestIdFromSession();
        setupHotelChainSpinner();
        setupDatePickers();
        setupPaymentRadio();
        setupConfirmButton();
    }

    private void initViews() {
        etCheckIn = findViewById(R.id.etCheckIn);
        etCheckOut = findViewById(R.id.etCheckOut);
        tvNights = findViewById(R.id.tvNights);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        rgPaymentType = findViewById(R.id.rgPaymentType);
        spinnerHotelChain = findViewById(R.id.spinnerHotelChain);
        spinnerHotels = findViewById(R.id.spinnerHotels);

        etCheckInTime = findViewById(R.id.etCheckInTime);
        etCheckOutTime = findViewById(R.id.etCheckOutTime);

        // Hiển thị giờ mặc định
        etCheckInTime.setText(String.format("%02d:%02d", checkInHour, checkInMinute));
        etCheckOutTime.setText(String.format("%02d:%02d", checkOutHour, checkOutMinute));

        // === BẮT CLICK CHỌN GIỜ ===
        etCheckInTime.setOnClickListener(v -> showTimePicker(true));
        etCheckOutTime.setOnClickListener(v -> showTimePicker(false));

        // QUAN TRỌNG: Lấy đúng LinearLayout chứa phòng
        containerRooms = findViewById(R.id.containerRooms);
        // Thêm dòng này để ScrollView có thanh cuộn đẹp
        scrollRooms = findViewById(R.id.scrollRooms);
        scrollRooms.setOverScrollMode(View.OVER_SCROLL_ALWAYS);

        containerServices = findViewById(R.id.containerServices);
        scrollServices = findViewById(R.id.scrollServices);
        scrollServices.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
    }

    private void getGuestIdFromSession() {
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        guestId = prefs.getInt("guest_id", -1);
        if (guestId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupHotelChainSpinner() {
        String[] chains = {"Best Western", "China Town", "Elite", "Cosmopolitan", "Prestige"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, chains);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHotelChain.setAdapter(adapter);

        spinnerHotelChain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int chainId = pos + 1;
                loadHotelsByChain(chainId);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerHotelChain.setSelection(0);
    }

    private void loadHotelsByChain(int chainId) {
        new Thread(() -> {
            List<Hotel> hotels = DatabaseHelper.getHotelsByChain(chainId);
            runOnUiThread(() -> {
                if (hotels == null || hotels.isEmpty()) {
                    Toast.makeText(this, "Không có khách sạn trong chuỗi này!", Toast.LENGTH_SHORT).show();
                    spinnerHotels.setAdapter(null);
                    selectedHotelId = -1;
                    clearRoomsAndServices();
                    return;
                }

                ArrayAdapter<Hotel> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, hotels);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerHotels.setAdapter(adapter);

                spinnerHotels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Hotel hotel = (Hotel) parent.getItemAtPosition(position);
                        selectedHotelId = hotel.hotelId;
                        Toast.makeText(CreateBookingActivity.this, "Đã chọn: " + hotel.hotelName, Toast.LENGTH_SHORT).show();
                        loadAvailableRooms();
                        loadServices();
                    }
                    @Override public void onNothingSelected(AdapterView<?> parent) {}
                });

                spinnerHotels.setSelection(0);
            });
        }).start();
    }

    private void setupDatePickers() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        etCheckIn.setOnClickListener(v -> showDatePicker(date -> {
            checkInDate = date;
            etCheckIn.setText(date.format(fmt));
            updateNightsAndRefresh();
        }));

        etCheckOut.setOnClickListener(v -> showDatePicker(date -> {
            checkOutDate = date;
            etCheckOut.setText(date.format(fmt));
            updateNightsAndRefresh();
        }));
    }

    private void showDatePicker(java.util.function.Consumer<LocalDate> callback) {
        LocalDate today = LocalDate.now();
        new DatePickerDialog(this, (view, y, m, d) -> {
            callback.accept(LocalDate.of(y, m + 1, d));
        }, today.getYear(), today.getMonthValue() - 1, today.getDayOfMonth()).show();
    }

    private void updateNightsAndRefresh() {
        if (checkInDate != null && checkOutDate != null && !checkInDate.isAfter(checkOutDate)) {
            nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            if (nights == 0) nights = 1; // ít nhất 1 đêm
            tvNights.setText("Số đêm: " + nights);
            loadAvailableRooms();
        } else {
            tvNights.setText("Số đêm: -");
        }
        calculateTotal();
    }

    // ĐÃ SỬA HOÀN HẢO – KÉO MƯỢT, KHÔNG LỖI
    private void loadAvailableRooms() {
        if (selectedHotelId == -1 || checkInDate == null || checkOutDate == null) {
            containerRooms.removeAllViews();
            return;
        }

        new Thread(() -> {
            List<Room> rooms = DatabaseHelper.getAvailableRooms(selectedHotelId, checkInDate, checkOutDate);

            runOnUiThread(() -> {
                containerRooms.removeAllViews();
                selectedRoomIds.clear();

                if (rooms.isEmpty()) {
                    TextView tv = new TextView(this);
                    tv.setText("Không có phòng trống trong khoảng thời gian này!");
                    tv.setPadding(40, 120, 40, 120);
                    tv.setGravity(android.view.Gravity.CENTER);
                    tv.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    tv.setTextSize(17);
                    containerRooms.addView(tv);
                    calculateTotal();
                    return;
                }

                // Format tiền Việt Nam đẹp: 103.000 (không có ₫ để tránh lỗi gạch ngang)
                DecimalFormat vnFormat = new DecimalFormat("#,##0");
                vnFormat.setGroupingSize(3);

                for (Room room : rooms) {
                    View item = getLayoutInflater().inflate(R.layout.item_room_simple, containerRooms, false);

                    TextView tvInfo = item.findViewById(R.id.tvRoomInfo);
                    TextView tvPrice = item.findViewById(R.id.tvPrice);
                    CheckBox cb = item.findViewById(R.id.cbSelectRoom);

                    tvInfo.setText("Phòng " + room.roomNumber + " - " + room.typeName);

                    // Chuẩn bị giá gốc và giá sau giảm
                    String originalPrice = vnFormat.format(room.originalPrice);
                    String finalPrice = vnFormat.format(room.getFinalPrice());

                    if (room.discountRate > 0) {
                        // Tạo chuỗi có gạch ngang chỉ ở phần giá gốc
                        String originalText = vnFormat.format(room.originalPrice) + " ₫";
                        String finalText = " → " + vnFormat.format(room.getFinalPrice()) + " ₫ (-" + (int)room.discountRate + "%)";

                        SpannableString spannable = new SpannableString(originalText + finalText);
                        spannable.setSpan(new StrikethroughSpan(), 0, originalText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        // Tùy chọn: làm giá gốc màu xám
                        spannable.setSpan(new ForegroundColorSpan(0xFF999999), 0, originalText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        tvPrice.setText(spannable);
                    } else {
                        tvPrice.setText(vnFormat.format(room.getFinalPrice()) + " ₫");
                    }

                    cb.setTag(room.roomId);
                    cb.setOnCheckedChangeListener((btn, isChecked) -> {
                        int roomId = (int) btn.getTag();
                        if (isChecked) {
                            selectedRoomIds.add(roomId);
                        } else {
                            selectedRoomIds.remove(Integer.valueOf(roomId));
                        }
                        calculateTotal();
                    });

                    containerRooms.addView(item);
                }
                calculateTotal();
            });
        }).start();
    }

    private void loadServices() {
        if (selectedHotelId == -1) {
            containerServices.removeAllViews();
            return;
        }

        new Thread(() -> {
            List<HotelService> services = DatabaseHelper.getServicesByHotel(selectedHotelId); // ĐÃ SỬA HÀM NÀY TRONG DB

            runOnUiThread(() -> {
                containerServices.removeAllViews();
                selectedServiceIds.clear();

                if (services.isEmpty()) {
                    TextView tv = new TextView(this);
                    tv.setText("Không có dịch vụ nào cho khách sạn này");
                    tv.setPadding(40, 80, 40, 80);
                    tv.setGravity(android.view.Gravity.CENTER);
                    tv.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    containerServices.addView(tv);
                    calculateTotal();
                    return;
                }

                NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

                for (HotelService service : services) {
                    View item = getLayoutInflater().inflate(R.layout.item_service_simple, containerServices, false);

                    TextView tvName = item.findViewById(R.id.tvServiceName);
                    TextView tvPrice = item.findViewById(R.id.tvServicePrice);
                    CheckBox cb = item.findViewById(R.id.cbSelectService);

                    tvName.setText(service.serviceName + (service.isCommon ? " (Dịch vụ chung)" : ""));
                    tvPrice.setText(fmt.format(service.cost));

                    cb.setTag(service.serviceId);
                    cb.setOnCheckedChangeListener((btn, isChecked) -> {
                        int sid = (int) btn.getTag();
                        if (isChecked) selectedServiceIds.add(sid);
                        else selectedServiceIds.remove(Integer.valueOf(sid));
                        calculateTotal();
                    });

                    containerServices.addView(item);
                }
                calculateTotal();
            });
        }).start();
    }

    private void clearRoomsAndServices() {
        // Xóa hết phòng
        containerRooms.removeAllViews();

        // Xóa hết dịch vụ (dịch vụ
        containerServices.removeAllViews();

        // Xóa danh sách đã chọn
        selectedRoomIds.clear();
        selectedServiceIds.clear();

        // Cập nhật lại tổng tiền
        calculateTotal();
    }

    private void setupPaymentRadio() {
        rgPaymentType.check(R.id.rbOnline);
    }

    private void calculateTotal() {
        // KHÔNG CẦN serviceAdapter NỮA → DỊCH VỤ ĐÃ ĐƯỢC CHỌN TRỰC TIẾP TRONG LIST
        // selectedServiceIds đã được cập nhật trong loadServices() khi tick checkbox

        if (selectedRoomIds.isEmpty()) {
            tvTotalAmount.setText("0 ₫");
            btnConfirmBooking.setEnabled(false);
            return;
        }

        if (nights <= 0) nights = 1;

        new Thread(() -> {
            try (Connection conn = DriverManager.getConnection(
                    DatabaseHelper.DB_URL, DatabaseHelper.DB_USER, DatabaseHelper.DB_PASS)) {

                // Tính tiền 1 đêm (phòng + dịch vụ)
                BigDecimal totalOneNight = DatabaseHelper.calculateTotalAmount(
                        conn, selectedRoomIds, selectedServiceIds, checkInDate, selectedHotelId);

                // NHÂN VỚI SỐ ĐÊM → CHÍNH XÁC 100%
                BigDecimal finalTotal = totalOneNight.multiply(BigDecimal.valueOf(nights));

                runOnUiThread(() -> {
                    DecimalFormat fmt = new DecimalFormat("#,##0.### ₫");
                    fmt.setMinimumFractionDigits(0);
                    fmt.setMaximumFractionDigits(3);
                    fmt.getDecimalFormatSymbols().setGroupingSeparator('.');

                    tvTotalAmount.setText(fmt.format(finalTotal));
                    btnConfirmBooking.setEnabled(true);
                });

            } catch (Exception e) {
                Log.e(TAG, "Lỗi tính tổng tiền", e);
                runOnUiThread(() -> tvTotalAmount.setText("Lỗi kết nối"));
            }
        }).start();
    }

    private void setupConfirmButton() {
        btnConfirmBooking.setOnClickListener(v -> {
            if (selectedRoomIds.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 phòng!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra ngày + giờ hợp lệ
            if (checkInDate == null || checkOutDate == null) {
                Toast.makeText(this, "Vui lòng chọn ngày nhận/trả phòng!", Toast.LENGTH_SHORT).show();
                return;
            }

            btnConfirmBooking.setEnabled(false);
            btnConfirmBooking.setText("Đang xử lý...");

            // Ghép ngày + giờ thành LocalDateTime chính xác
            LocalDateTime checkInDateTime = checkInDate.atTime(checkInHour, checkInMinute);
            LocalDateTime checkOutDateTime = checkOutDate.atTime(checkOutHour, checkOutMinute);

            // Kiểm tra giờ check-out không sớm hơn check-in
            if (!checkOutDateTime.isAfter(checkInDateTime)) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Giờ trả phòng phải sau giờ nhận phòng!", Toast.LENGTH_LONG).show();
                    btnConfirmBooking.setEnabled(true);
                    btnConfirmBooking.setText("Xác nhận đặt phòng");
                });
                return;
            }

            // Lấy phương thức thanh toán
            final String paymentType;
            int checked = rgPaymentType.getCheckedRadioButtonId();
            if (checked == R.id.rbCash) {
                paymentType = "cash";
            } else if (checked == R.id.rbCard) {
                paymentType = "card";
            } else {
                paymentType = "online";
            }

            new Thread(() -> {
                long bookingId = DatabaseHelper.createBooking(
                        guestId,
                        selectedHotelId,
                        checkInDateTime,      // truyền có giờ
                        checkOutDateTime,    // truyền có giờ
                        new ArrayList<>(selectedRoomIds),
                        new ArrayList<>(selectedServiceIds),
                        paymentType
                );

                runOnUiThread(() -> {
                    btnConfirmBooking.setEnabled(true);
                    btnConfirmBooking.setText("Xác nhận đặt phòng");

                    if (bookingId > 0) {
                        Toast.makeText(this,
                                "ĐẶT PHÒNG THÀNH CÔNG!\nMã đặt phòng: " + bookingId,
                                Toast.LENGTH_LONG).show();

                        // Có thể chuyển sang màn hình xác nhận booking
                        // Intent intent = new Intent(this, BookingSuccessActivity.class);
                        // intent.putExtra("booking_id", bookingId);
                        // startActivity(intent);

                        finish(); // quay lại màn hình trước
                    } else {
                        Toast.makeText(this, "Đặt phòng thất bại! Vui lòng thử lại.", Toast.LENGTH_LONG).show();
                    }
                });
            }).start();
        });
    }

    // ===== THÊM HÀM CHỌN GIỜ (TimePicker) =====
    private void showTimePicker(boolean isCheckIn) {
        int hour = isCheckIn ? checkInHour : checkOutHour;
        int minute = isCheckIn ? checkInMinute : checkOutMinute;

        new android.app.TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    if (isCheckIn) {
                        checkInHour = hourOfDay;
                        checkInMinute = minuteOfHour;
                        etCheckInTime.setText(String.format("%02d:%02d", hourOfDay, minuteOfHour));
                    } else {
                        checkOutHour = hourOfDay;
                        checkOutMinute = minuteOfHour;
                        etCheckOutTime.setText(String.format("%02d:%02d", hourOfDay, minuteOfHour));
                    }
                    // Tính lại tổng tiền nếu cần (hiện tại chưa tính theo giờ)
                    calculateTotal();
                },
                hour, minute, true).show();
    }
}