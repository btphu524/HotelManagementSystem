package com.example.hotelmanagementsystem.form1;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelmanagementsystem.DatabaseHelper;
import com.example.hotelmanagementsystem.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class CreateGuestActivity extends AppCompatActivity {

    private TextInputEditText etFirstName, etLastName, etPhone, etEmail, etCreditCard, etAddressInput;
    private MaterialButton btnChooseImage, btnSave;
    private ImageView imgPreview;
    private TextView tvImageStatus;

    private Uri imageUri = null;
    private String finalImageUrl = null;
    private final DatabaseHelper dbHelper = new DatabaseHelper();

    // Launcher chọn ảnh CMND/CCCD
    // Launcher chọn ảnh – ĐÃ FIX HOÀN TOÀN
    private final ActivityResultLauncher<Intent> imagePicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();

                    // Giữ quyền truy cập lâu dài (nếu được phép)
                    int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    try {
                        getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
                    } catch (Exception e) {
                        e.printStackTrace(); // Không chết app nếu không persist được
                    }

                    // Hiển thị ảnh preview
                    imgPreview.setImageURI(imageUri);
                    imgPreview.setVisibility(ImageView.VISIBLE);

                    // Tạo tên file lưu vào DB
                    String fileName = getFileName(imageUri);
                    finalImageUrl = "/images/" + fileName;

                    tvImageStatus.setText("Đã chọn: " + fileName);
                    tvImageStatus.setTextColor(getColor(android.R.color.holo_green_dark));
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_guest);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etCreditCard = findViewById(R.id.etCreditCard);
        etAddressInput = findViewById(R.id.etAddressInput);

        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSave = findViewById(R.id.btnSave);
        tvImageStatus = findViewById(R.id.tvImageStatus);
        imgPreview = findViewById(R.id.imgPreview);
    }

    private void setupListeners() {
        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            imagePicker.launch(intent);
        });

        btnSave.setOnClickListener(v -> validateAndSave());
    }

    private void validateAndSave() {
        String rawFirstName = getTextOrEmpty(etFirstName);
        String rawLastName = getTextOrEmpty(etLastName);
        String phone = getTextOrEmpty(etPhone);
        String email = getTextOrEmpty(etEmail).toLowerCase();
        String creditCard = getTextOrEmpty(etCreditCard);
        String addressInput = getTextOrEmpty(etAddressInput);

        // 1. Họ và tên: chuẩn hóa + validate
        String firstName = capitalizeWords(rawFirstName);
        String lastName = capitalizeWords(rawLastName);

        if (rawFirstName.isEmpty()) {
            etFirstName.setError("Vui lòng nhập họ");
            etFirstName.requestFocus();
            return;
        }
        if (!firstName.matches("[a-zA-Z\\s]+")) {
            etFirstName.setError("Họ chỉ được chứa chữ cái");
            etFirstName.requestFocus();
            return;
        }
        if (rawLastName.isEmpty()) {
            etLastName.setError("Vui lòng nhập tên");
            etLastName.requestFocus();
            return;
        }
        if (!lastName.matches("[a-zA-Z\\s]+")) {
            etLastName.setError("Tên chỉ được chứa chữ cái");
            etLastName.requestFocus();
            return;
        }

        // 2. Số điện thoại: đúng 10 số, bắt đầu bằng 0
        if (!phone.matches("0\\d{9}")) {
            etPhone.setError("Số điện thoại phải có 10 số và bắt đầu bằng 0");
            etPhone.requestFocus();
            return;
        }

        // 3. Email: phải có @ và domain hợp lệ
        if (!isValidEmail(email)) {
            etEmail.setError("Email không hợp lệ. Ví dụ: abc@gmail.com");
            etEmail.requestFocus();
            return;
        }

        // 4. Địa chỉ
        if (addressInput.isEmpty()) {
            etAddressInput.setError("Vui lòng nhập địa chỉ");
            etAddressInput.requestFocus();
            return;
        }

        // 5. Ảnh CMND
        if (imageUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh CMND/CCCD", Toast.LENGTH_LONG).show();
            return;
        }

        // 6. Credit card (tùy chọn) – nếu có thì phải đúng định dạng
        if (!creditCard.isEmpty() && !creditCard.matches("\\d{16}|\\d{4}\\s\\d{4}\\s\\d{4}\\s\\d{4}")) {
            etCreditCard.setError("Số thẻ phải 16 số (có thể có dấu cách)");
            etCreditCard.requestFocus();
            return;
        }

        // === TẤT CẢ ĐÃ HỢP LỆ → LƯU ===
        btnSave.setEnabled(false);
        btnSave.setText("ĐANG LƯU...");

        new Thread(() -> {
            int guestId = dbHelper.createGuest(
                    firstName,
                    lastName,
                    phone,
                    email,
                    creditCard.isEmpty() ? null : creditCard.replaceAll("\\s", ""), // bỏ dấu cách trước khi lưu
                    finalImageUrl,
                    addressInput
            );

            runOnUiThread(() -> {
                btnSave.setEnabled(true);
                btnSave.setText("LƯU KHÁCH HÀNG");

                if (guestId > 0) {
                    Toast.makeText(this,
                            "Tạo khách hàng thành công!\nID: " + guestId + "\nTên: " + firstName + " " + lastName,
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this, "Lưu thất bại! Có thể email đã được dùng.", Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }

    // Hàm viết hoa chữ cái đầu mỗi từ (VD: nguyen van nam → Nguyen Van Nam)
    private String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) return "";
        String[] words = str.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return sb.toString().trim();
    }

    // Kiểm tra email hợp lệ + domain phổ biến
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(com|vn|net|org|edu|gov|info|co)$";
        if (!email.matches(regex)) return false;

        // Danh sách domain được phép (mở rộng thoải mái)
        String[] allowedDomains = {"gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "icloud.com", "yandex.com", "protonmail.com"};
        String domain = email.substring(email.indexOf("@") + 1);
        for (String d : allowedDomains) {
            if (domain.equalsIgnoreCase(d)) return true;
        }
        return false;
    }

    // Lấy text an toàn từ EditText
    private String getTextOrEmpty(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    // Lấy tên file thật từ Uri
    private String getFileName(Uri uri) {
        String result = "id_proof_" + System.currentTimeMillis() + ".jpg";
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index != -1) {
                    String name = cursor.getString(index);
                    if (name != null && !name.trim().isEmpty()) {
                        result = name.trim();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}