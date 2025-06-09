package com.kiyyaaa.cinespot;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kiyyaaa.cinespot.sqlite.DbConfig;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView registerText;
    private DbConfig dbConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbConfig = new DbConfig(this);

        usernameEditText = findViewById(R.id.editTextText);
        passwordEditText = findViewById(R.id.editTextPass);
        loginButton = findViewById(R.id.loginBtn);
        registerText = findViewById(R.id.textView8);

        loginButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty()) {
                usernameEditText.setError("Tolong Masukkan Username");
                usernameEditText.requestFocus();
            } else if (password.isEmpty()) {
                passwordEditText.setError("Tolong Masukkan Username");
                passwordEditText.requestFocus();
            } else {
                login(username, password);
            }
        });

        registerText.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLoginStatus();
    }

    // memeriksa apakah ada user yang masih logged in
    private void checkLoginStatus() {
        try (SQLiteDatabase db = dbConfig.getReadableDatabase();
             Cursor cursor = db.query(
                     DbConfig.TABLE_NAME,
                     new String[]{DbConfig.COLUMN_ID},
                     DbConfig.COLUMN_IS_LOGGED_IN + " = ?",
                     new String[]{"1"},
                     null, null, null)) {

            if (cursor.getCount() > 0) {
                // Jika sudah ada yang login, langsung menuju MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    // Metode untuk melakukan proses login
    private void login(String username, String password) {
        try (SQLiteDatabase db = dbConfig.getReadableDatabase();
             Cursor cursor = db.query(
                     DbConfig.TABLE_NAME,
                     new String[]{DbConfig.COLUMN_ID},
                     DbConfig.COLUMN_USERNAME + " = ? AND " + DbConfig.COLUMN_PASSWORD + " = ?",
                     new String[]{username, password},
                     null, null, null)) {

            if (cursor.moveToFirst()) {
                // Jika ditemukan, perbarui status login dan menuju MainActivity
                updateLoginStatus(username, true);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Incorrect username or Password", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Metode untuk memperbarui kolom is_logged_in (0/1)
    private void updateLoginStatus(String username, boolean isLoggedIn) {
        try (SQLiteDatabase db = dbConfig.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DbConfig.COLUMN_IS_LOGGED_IN, isLoggedIn ? 1 : 0);
            db.update(
                    DbConfig.TABLE_NAME,
                    values,
                    DbConfig.COLUMN_USERNAME + " = ?",
                    new String[]{username}
            );
        }
    }
}
