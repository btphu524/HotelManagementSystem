package com.example.hotelmanagementsystem.form2;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagementsystem.DatabaseHelper;
import com.example.hotelmanagementsystem.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.NumberFormat;
import java.time.LocalDate;
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
    private LinearLayout containerRooms;  // ← ĐÃ KHAI BÁO ĐÚNG

    private LocalDate checkInDate, checkOutDate;
    private int selectedHotelId = -1;
    private int guestId = -1;

    private final List<Integer> selectedRoomIds = new ArrayList<>();
    private final List<Integer> selectedServiceIds = new ArrayList<>();

    private ServiceAdapter serviceAdapter;

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

        // QUAN TRỌNG: Lấy đúng LinearLayout chứa phòng
        containerRooms = findViewById(R.id.containerRooms);
        // Thêm dòng này để ScrollView có thanh cuộn đẹp
        ScrollView scrollRooms = findViewById(R.id.scrollRooms);
        scrollRooms.setOverScrollMode(View.OVER_SCROLL_ALWAYS);

        // Dịch vụ vẫn dùng RecyclerView (vì ít item, không bị lỗi scroll)
        RecyclerView rvServices = findViewById(R.id.rvServices);
        rvServices.setLayoutManager(new LinearLayoutManager(this));
        rvServices.setNestedScrollingEnabled(false);
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
            long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
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
                    tv.setPadding(40, 100, 40, 100);
                    tv.setGravity(android.view.Gravity.CENTER);
                    tv.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    tv.setTextSize(16);
                    containerRooms.addView(tv);
                    calculateTotal();
                    return;
                }

                NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

                for (Room room : rooms) {
                    View item = getLayoutInflater().inflate(R.layout.item_room_simple, containerRooms, false);

                    TextView tvInfo = item.findViewById(R.id.tvRoomInfo);
                    TextView tvPrice = item.findViewById(R.id.tvPrice);
                    CheckBox cb = item.findViewById(R.id.cbSelectRoom);

                    tvInfo.setText("Phòng " + room.roomNumber + " - " + room.typeName);

                    if (room.discountRate > 0) {
                        tvPrice.setText(fmt.format(room.originalPrice) + " → " + fmt.format(room.getFinalPrice()) + " (-" + (int)room.discountRate + "%)");
                        tvPrice.setPaintFlags(tvPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        tvPrice.setText(fmt.format(room.getFinalPrice()));
                    }

                    cb.setTag(room.roomId);
                    cb.setOnCheckedChangeListener((btn, isChecked) -> {
                        int roomId = (int) btn.getTag();
                        if (isChecked) selectedRoomIds.add(roomId);
                        else selectedRoomIds.remove(Integer.valueOf(roomId));
                        calculateTotal();
                    });

                    containerRooms.addView(item);
                }
                calculateTotal();
            });
        }).start();
    }

    private void loadServices() {
        if (selectedHotelId == -1) return;

        new Thread(() -> {
            List<HotelService> services = DatabaseHelper.getServicesByHotel(selectedHotelId);
            runOnUiThread(() -> {
                serviceAdapter = new ServiceAdapter(services, this::calculateTotal);
                RecyclerView rvServices = findViewById(R.id.rvServices);
                rvServices.setAdapter(serviceAdapter);
                calculateTotal();
            });
        }).start();
    }

    private void clearRoomsAndServices() {
        containerRooms.removeAllViews();
        RecyclerView rvServices = findViewById(R.id.rvServices);
        rvServices.setAdapter(new ServiceAdapter(new ArrayList<>(), this::calculateTotal));
        calculateTotal();
    }

    private void setupPaymentRadio() {
        rgPaymentType.check(R.id.rbOnline);
    }

    private void calculateTotal() {
        selectedServiceIds.clear();
        if (serviceAdapter != null) {
            selectedServiceIds.addAll(serviceAdapter.getSelectedServiceIds());
        }

        if (selectedRoomIds.isEmpty()) {
            tvTotalAmount.setText("0 ₫");
            btnConfirmBooking.setEnabled(false);
            return;
        }

        new Thread(() -> {
            try (Connection conn = DriverManager.getConnection(
                    DatabaseHelper.DB_URL, DatabaseHelper.DB_USER, DatabaseHelper.DB_PASS)) {

                BigDecimal total = DatabaseHelper.calculateTotalAmount(conn, selectedRoomIds, selectedServiceIds, checkInDate, selectedHotelId);

                runOnUiThread(() -> {
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                    tvTotalAmount.setText(fmt.format(total));
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

            btnConfirmBooking.setEnabled(false);

            // LẤY paymentType MỘT LẦN DUY NHẤT → ĐẢM BẢO FINAL
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
                        checkInDate,
                        checkOutDate,
                        new ArrayList<>(selectedRoomIds),
                        new ArrayList<>(selectedServiceIds),
                        paymentType  // Bây giờ đã final → không lỗi nữa!
                );

                runOnUiThread(() -> {
                    btnConfirmBooking.setEnabled(true);
                    if (bookingId > 0) {
                        Toast.makeText(this, "ĐẶT PHÒNG THÀNH CÔNG! Mã booking: " + bookingId, Toast.LENGTH_LONG).show();
                        // Chuyển sang màn hình xác nhận (tùy bạn)
                        finish();
                    } else {
                        Toast.makeText(this, "Đặt phòng thất bại!", Toast.LENGTH_LONG).show();
                    }
                });
            }).start();
        });
    }
}