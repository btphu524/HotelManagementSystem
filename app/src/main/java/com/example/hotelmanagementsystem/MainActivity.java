package com.example.hotelmanagementsystem;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.hotelmanagementsystem.form1.CreateGuestActivity;
import com.example.hotelmanagementsystem.form2.LoginGuestActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnForm1 = findViewById(R.id.btnForm1);
        Button btnForm2 = findViewById(R.id.btnForm2);

        btnForm1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateGuestActivity.class);
            startActivity(intent);
        });

        btnForm2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginGuestActivity.class);
            startActivity(intent);
        });
    }
}