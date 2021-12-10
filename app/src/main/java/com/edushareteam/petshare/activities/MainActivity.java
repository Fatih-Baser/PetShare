package com.edushareteam.petshare.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.edushareteam.petshare.R;
import com.edushareteam.petshare.databinding.ActivityMainBinding;
import com.edushareteam.petshare.providers.AuthProvider;
import com.edushareteam.petshare.providers.UsersProvider;

import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;
    private ActivityMainBinding binding;

    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Giriş Yapılıyor ...")
                .setCancelable(false).build();


        binding.createAccount.setOnClickListener(view1 -> {
           Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        binding.loginAccount.setOnClickListener(view12 -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

    }



    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthProvider.getUserSession() != null) {
           Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


}