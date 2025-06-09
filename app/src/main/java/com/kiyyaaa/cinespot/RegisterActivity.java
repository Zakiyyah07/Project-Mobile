package com.kiyyaaa.cinespot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.kiyyaaa.cinespot.sqlite.DbConfig;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullNameEditText, emailEditText, usernameEditText, passwordEditText, confirmPasswordEditText;
    private CheckBox termsCheckBox;
    private AppCompatButton registerButton;
    private TextView loginNowTextView;
    private DbConfig dbConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbConfig = new DbConfig(this);

        fullNameEditText = findViewById(R.id.editTextFullName);
        emailEditText = findViewById(R.id.editTextEmail);
        usernameEditText = findViewById(R.id.editTextUserName);
        passwordEditText = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        termsCheckBox = findViewById(R.id.checkBoxTerms);
        registerButton = findViewById(R.id.registerBtn);
        loginNowTextView = findViewById(R.id.textViewLoginNow);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fullName = fullNameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if (fullName.isEmpty()) {
                    fullNameEditText.setError("Full Name wajib diisi");
                    fullNameEditText.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    emailEditText.setError("Email wajib diisi");
                    emailEditText.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.setError("Format email tidak valid");
                    emailEditText.requestFocus();
                    return;
                }

                if (username.isEmpty()) {
                    usernameEditText.setError("Username wajib diisi");
                    usernameEditText.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    passwordEditText.setError("Password wajib diisi");
                    passwordEditText.requestFocus();
                    return;
                }
                if (confirmPassword.isEmpty()) {
                    confirmPasswordEditText.setError("Konfirmasi password wajib diisi");
                    confirmPasswordEditText.requestFocus();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    confirmPasswordEditText.setError("Password tidak sama");
                    confirmPasswordEditText.requestFocus();
                    return;
                }

                if (!termsCheckBox.isChecked()) {
                    Toast.makeText(RegisterActivity.this, "Anda harus menyetujui Terms and Conditions", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dbConfig.isUsernameExists(username)) {
                    usernameEditText.setError("Username sudah terdaftar");
                    usernameEditText.requestFocus();
                    Toast.makeText(RegisterActivity.this, "Username sudah terdaftar", Toast.LENGTH_SHORT).show();
                } else {
                    dbConfig.insertData(username, password,fullName,email);
                        Toast.makeText(RegisterActivity.this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                }
            }
        });

        loginNowTextView.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
