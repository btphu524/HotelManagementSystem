package com.example.hotelmanagementsystem.form2; // sửa đúng package

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.hotelmanagementsystem.DatabaseHelper;
import com.example.hotelmanagementsystem.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginGuestActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_guest);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String email = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);

        new Thread(() -> {
            GuestLoginResult result = DatabaseHelper.loginAndGetGuestInfo(email, password);

            runOnUiThread(() -> {
                btnLogin.setEnabled(true);

                if (result != null && result.guestId > 0) {
                    SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
                    prefs.edit()
                            .putInt("guest_id", result.guestId)
                            .putString("first_name", result.firstName)
                            .putString("last_name", result.lastName)
                            .putString("email", result.email)
                            .putString("phone", result.phone)
                            .putBoolean("is_logged_in", true)
                            .apply();

                    Toast.makeText(this, "Chào " + result.getFullName() + "!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, CreateBookingActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Sai email hoặc mật khẩu", Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }
}