package com.edushareteam.petshare.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.edushareteam.petshare.databinding.ActivityAddPublicationsBinding;
import com.edushareteam.petshare.models.Request;
import com.edushareteam.petshare.models.User;
import com.edushareteam.petshare.providers.AuthProvider;
import com.edushareteam.petshare.providers.ImageProvider;
import com.edushareteam.petshare.providers.RequestProvider;
import com.edushareteam.petshare.providers.UsersProvider;
import com.edushareteam.petshare.utils.ViewedMessageHelper;

import java.io.File;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class AddPublicationsActivity extends AppCompatActivity {

    private ActivityAddPublicationsBinding binding;
    //Providers
    ImageProvider mImageProvider;
    RequestProvider mPostProvider;
    AuthProvider mAuthProvider;
    UsersProvider mUserProvider;

    String currentDateString;
    File mImageFile;
    File mImageFile2;

    String mCategory = "";
    String mImage = "";
    String mTitle = "";
    float mQuality = 0;
    String mSpinnerCategories = "";
    String mDescription = "";
    AlertDialog mDialog;
    AlertDialog.Builder mBuilderSelector;
    CharSequence[] options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPublicationsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mImageProvider = new ImageProvider();
        mPostProvider = new RequestProvider();
        mAuthProvider = new AuthProvider();
        mUserProvider = new UsersProvider();

        mUserProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                if (documentSnapshot.contains("image")) {
                    mImage = documentSnapshot.getString("image");
                }
            }
        });
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Biraz bekle ")
                .setCancelable(false).build();

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Lütfen bir seçenek seçiniz");
        options = new CharSequence[]{"Galeriden resim seç", "Fotograf çek"};


        binding.btnPost.setOnClickListener(view12 -> clickPost());

    }


    private void clickPost() {
        mTitle = Objects.requireNonNull(binding.textInputVideoGame.getText()).toString();
        mDescription = Objects.requireNonNull(binding.textInputDescription.getText()).toString();
        saveImage();
    }

    private void saveImage() {
        mDialog.show();

        String uuid = UUID.randomUUID().toString();
        User user = new User();
        Request request = new Request();
        request.setId(uuid);
        request.setImageProfile(mImage);
        request.setTitle(mTitle);
        request.setBio(mDescription);
        request.setIdUser(mAuthProvider.getUid());
        request.setTimestamp(new Date().getTime());
        mPostProvider.save(request).addOnCompleteListener(taskSave -> {
            mDialog.dismiss();
            if (taskSave.isSuccessful()) {
                // clearForm();
                Toast.makeText(AddPublicationsActivity.this, "Bilgiler doğru bir şekilde saklandı", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddPublicationsActivity.this, HomeActivity.class);

                startActivity(intent);

            } else {
                Toast.makeText(AddPublicationsActivity.this, "Bilgiler saklanamadı", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void clearForm() {
        binding.textInputVideoGame.setText("");
        binding.textInputDescription.setText("");
        mTitle = "";
        mDescription = "";
        mSpinnerCategories = "";
        mImageFile = null;
        mImageFile2 = null;
    }


    @Override
    public void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, AddPublicationsActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, AddPublicationsActivity.this);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}