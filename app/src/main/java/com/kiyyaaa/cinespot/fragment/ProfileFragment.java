package com.kiyyaaa.cinespot.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.kiyyaaa.cinespot.AboutActivity;
import com.kiyyaaa.cinespot.LoginActivity;
import com.kiyyaaa.cinespot.PrivacyActivity;
import com.kiyyaaa.cinespot.R;
import com.kiyyaaa.cinespot.sqlite.DbConfig;

public class ProfileFragment extends Fragment {

    private View aboutCard, privacyCard;

    private TextView fullNameView, usernameView, emailView;
    private Button logoutBtn;
    private Switch swTema;
    private DbConfig dbConfig;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String PREFS_NAME = "theme_pref";
    private static final String KEY_DARK_MODE = "is_dark_theme";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fullNameView = view.findViewById(R.id.tvFullName);
        usernameView = view.findViewById(R.id.tvUsername);
        emailView = view.findViewById(R.id.tvEmail);
        logoutBtn = view.findViewById(R.id.btnLogout);
        swTema = view.findViewById(R.id.switchDarkTheme);
        aboutCard   = view.findViewById(R.id.aboutCard);
        privacyCard = view.findViewById(R.id.privacyCard);

        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        boolean isDarkTheme = sharedPreferences.getBoolean(KEY_DARK_MODE, false);
        swTema.setChecked(isDarkTheme);

        swTema.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Simpan dan toggle tema
            editor.putBoolean(KEY_DARK_MODE, isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked
                            ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        // Inisialisasi database helper
        dbConfig = new DbConfig(requireContext());
        loadUserData();

        logoutBtn.setOnClickListener(v -> {
            // Set is_logged_in = 0
            try (SQLiteDatabase db = dbConfig.getWritableDatabase()) {
                ContentValues values = new ContentValues();
                values.put(DbConfig.COLUMN_IS_LOGGED_IN, 0);
                db.update(
                        DbConfig.TABLE_NAME,
                        values,
                        DbConfig.COLUMN_IS_LOGGED_IN + " = ?",
                        new String[]{"1"}
                );
            }
            startActivity(new Intent(requireActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        aboutCard.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AboutActivity.class);
            startActivity(intent);
        });

        privacyCard.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PrivacyActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserData() {
        try (SQLiteDatabase db = dbConfig.getReadableDatabase();
             Cursor cursor = db.query(
                     DbConfig.TABLE_NAME,
                     new String[]{
                             DbConfig.COLUMN_ID,
                             DbConfig.COLUMN_FULL_NAME,
                             DbConfig.COLUMN_USERNAME,
                             DbConfig.COLUMN_EMAIL
                     },
                     DbConfig.COLUMN_IS_LOGGED_IN + " = ?",
                     new String[]{"1"},
                     null, null, null)) {

            if (cursor.moveToFirst()) {
                fullNameView.setText(
                        cursor.getString(cursor.getColumnIndexOrThrow(DbConfig.COLUMN_FULL_NAME))
                );
                usernameView.setText(
                        cursor.getString(cursor.getColumnIndexOrThrow(DbConfig.COLUMN_USERNAME))
                );
                emailView.setText(
                        cursor.getString(cursor.getColumnIndexOrThrow(DbConfig.COLUMN_EMAIL))
                );
            }
        }
    }
}
