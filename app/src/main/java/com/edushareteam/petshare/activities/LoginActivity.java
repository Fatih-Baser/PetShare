package com.edushareteam.petshare.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.edushareteam.petshare.R;
import com.edushareteam.petshare.databinding.ActivityLoginBinding;
import com.edushareteam.petshare.providers.AuthProvider;
import com.edushareteam.petshare.providers.UsersProvider;

import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Giriş Yapılıyor ...")
                .setCancelable(false).build();



        binding.btnLogin.setOnClickListener(view1 -> login());

        binding.back.setOnClickListener(view14 -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        });

        binding.textViewReset.setOnClickListener(view13 -> {
            final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view13.getContext());
            View view1 = getLayoutInflater().inflate(R.layout.dialog_reset,null);

            final EditText resetMail = view1.findViewById(R.id.textInputEmailReset);
            Button button = view1.findViewById(R.id.button_dialog);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (!resetMail.getText().toString().isEmpty()){
                            String mail = resetMail.getText().toString();
                            mAuthProvider.resetPassword(mail);
                            Toast.makeText(getApplicationContext(),
                                    "Please check your e-mail account for the password reset link.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),
                                    "Please enter a valid e-mail address.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            passwordResetDialog.setView(view1);
            passwordResetDialog.create().show();
        });



        binding.textViewRegister.setOnClickListener(view12 -> {
           Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthProvider.getUserSession() != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }



    private void login() {
        String email = Objects.requireNonNull(binding.textInputEmail.getText()).toString();
        String password = Objects.requireNonNull(binding.textInputPassword.getText()).toString();
        if(!email.isEmpty() && !password.isEmpty() ) {
            mDialog.show();
            mAuthProvider.login(email, password).addOnCompleteListener(task -> {
                mDialog.dismiss();
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {
                    Toast.makeText(LoginActivity.this, "The email or password you entered is not correct", Toast.LENGTH_LONG).show();
                }
            });
        }else{
            Toast.makeText(LoginActivity.this, "Please fill in the blanks!", Toast.LENGTH_LONG).show();
        }
    }
}