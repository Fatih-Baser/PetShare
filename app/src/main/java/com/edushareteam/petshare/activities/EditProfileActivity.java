package com.edushareteam.petshare.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.edushareteam.petshare.R;
import com.edushareteam.petshare.databinding.ActivityEditProfileBinding;
import com.edushareteam.petshare.models.User;
import com.edushareteam.petshare.providers.AuthProvider;
import com.edushareteam.petshare.providers.ImageProvider;
import com.edushareteam.petshare.providers.UsersProvider;
import com.edushareteam.petshare.utils.FileUtil;
import com.edushareteam.petshare.utils.ViewedMessageHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    AlertDialog.Builder mBuilderSelector;
    AlertDialog mDialog;
    CharSequence[] options;
    private final int GALLERY_REQUEST_CODE_PROFILE = 1;
    private final int PHOTO_REQUEST_CODE_PROFILE = 3;

    // FOTO 1
    String mAbsolutePhotoPath;
    String mPhotoPath;
    File mPhotoFile;
    File mImageFile;

    String mUsername = "";
    String mUniversity = "";

    String mBio = "";
    String mImageProfile = "";
    String mImage;
    //Providers
    ImageProvider mImageProvider;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;

    //Spınner
    ValueEventListener valueEventListener;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> spinnerDataList;

    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Lütfen bir seçenek seçiniz");
        options = new CharSequence[] {"Galeriden resim seç ","Fotoğraf çek"};
        databaseReference = FirebaseDatabase.getInstance().getReference("cities");

        mImageProvider = new ImageProvider();
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();

        mUsersProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                if (documentSnapshot.contains("image")) {
                    mImage = documentSnapshot.getString("image");
                }
            }
        });

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Biraz bekle")
                .setCancelable(false).build();
        binding.btnEditProfile.setOnClickListener(view13 -> clickEditProfile());

        binding.circleImageProfile.setOnClickListener(view12 -> selectOptionImage(1));

        binding.circleImageBack.setOnClickListener(view1 -> finish());
        spinnerDataList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(EditProfileActivity.this,
                android.R.layout.simple_spinner_dropdown_item, spinnerDataList);
        binding.spinnerProductCategory.setAdapter(arrayAdapter);
        retrieveData();
        getUser();
    }

    private void retrieveData() {

        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item :snapshot.getChildren()){
                    spinnerDataList.add(item.child("name").getValue().toString());
                }


                arrayAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void getUser() {
        mUsersProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("username")) {
                    mUsername = documentSnapshot.getString("username");
                    binding.textInputUsername.setText(mUsername);
                }


                if (documentSnapshot.contains("city")) {
                    mUniversity = documentSnapshot.getString("city");
                    spinnerDataList.set(0,mUniversity);
                    arrayAdapter.notifyDataSetChanged();
                }
                if (documentSnapshot.contains("bio")) {
                    mBio = documentSnapshot.getString("bio");
                    binding.textInputBio.setText(mBio);
                }
                if (documentSnapshot.contains("image")) {
                    mImageProfile = documentSnapshot.getString("image");
                    if (mImageProfile != null) {
                        if (!mImageProfile.isEmpty()) {
                            Picasso.with(EditProfileActivity.this).load(mImageProfile).into(binding.circleImageProfile);
                        }
                    }
                }
            }
        });
    }

    private void clickEditProfile() {
        mUsername = Objects.requireNonNull(binding.textInputUsername.getText()).toString();
        mUniversity = Objects.requireNonNull(binding.spinnerProductCategory.getSelectedItem()).toString();

        mBio = Objects.requireNonNull(binding.textInputBio.getText()).toString();
        if (!mUsername.isEmpty() && !mUniversity.isEmpty()&&  !mBio.isEmpty()) {
            if (mImageFile != null) {
                saveImageFirebaseStorage(mImageFile);
            }
            //KAMERANIN İKİ RESİMİNİ ÇEKİYORUM
            else if (mPhotoFile != null) {
                saveImageFirebaseStorage(mPhotoFile);
            }
            else if (mPhotoFile != null) {
                saveImage(mPhotoFile, true);
            }
            else if (mImageFile != null) {
                saveImage(mImageFile, true);
            }
            else {
                User user = new User();
                user.setUsername(mUsername);
                user.setCity(mUniversity);
                user.setBio(mBio);
                user.setId(mAuthProvider.getUid());
                user.setImage(mImage);
                updateInfo(user);
            }
        }
        else {
            Toast.makeText(this, "Kullanıcı adını ve diğer bılgileri giriniz", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageFirebaseStorage(File imageFile1) {
        mDialog.show();
        mImageProvider.save(EditProfileActivity.this, imageFile1).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    final String urlProfile = uri.toString();
                    User user = new User();
                    user.setImage(urlProfile);
                    user.setUsername(mUsername);
                    user.setCity(mUniversity);

                    user.setBio(mBio);
                    user.setId(mAuthProvider.getUid());
                    updateInfo(user);
                });
            }
            else {
                mDialog.dismiss();
                Toast.makeText(EditProfileActivity.this, "Görüntü kaydedilirken bir hata oluştu", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveImage(File image, final boolean isProfileImage) {
        mDialog.show();
        mImageProvider.save(EditProfileActivity.this, image).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    final String url = uri.toString();
                    User user = new User();
                    user.setUsername(mUsername);
                    user.setCity(mUniversity);

                    user.setBio(mBio);
                    user.setId(mAuthProvider.getUid());
                    updateInfo(user);
                    if (isProfileImage) {
                        user.setImage(url);
                    }
                    else {
                        user.setImage(mImageProfile);
                    }
                });
            }
            else {
                mDialog.dismiss();
                Toast.makeText(EditProfileActivity.this, "Görüntü kaydedilirken bir hata oluştu", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateInfo(User user) {
        if (mDialog.isShowing()) {
            mDialog.show();
        }
        mUsersProvider.updateProfile(user).addOnCompleteListener(task -> {
            mDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(EditProfileActivity.this, "Bilgiler doğru bir şekilde güncellendi", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(EditProfileActivity.this, "Bilgiler güncellenemedi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectOptionImage(final int numberImage) {

        mBuilderSelector.setItems(options, (dialogInterface, i) -> {
            if (i == 0) {
                if (numberImage == 1) {
                    openGallery(GALLERY_REQUEST_CODE_PROFILE);
                }
            }
            else if (i == 1){
                if (numberImage == 1) {
                    takePhoto(PHOTO_REQUEST_CODE_PROFILE);
                }
            }
        });
        mBuilderSelector.show();
    }

    private void takePhoto(int requestCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createPhotoFile(requestCode);
            } catch(Exception e) {
                Toast.makeText(this, "Dosyada bir hata oluştu " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(EditProfileActivity.this, "com.fatihbaser.edusharedemo", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, requestCode);
            }
        }
    }

    private File createPhotoFile(int requestCode) throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                new Date() + "_photo",
                ".jpg",
                storageDir
        );
        if (requestCode == PHOTO_REQUEST_CODE_PROFILE) {
            mPhotoPath = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath = photoFile.getAbsolutePath();
        }
        return photoFile;
    }

    private void openGallery(int requestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * GALERİDEN GÖRÜNTÜ SEÇİMİ
         */
        if (requestCode == GALLERY_REQUEST_CODE_PROFILE && resultCode == RESULT_OK) {
            try {
                mPhotoFile = null;
                mImageFile = FileUtil.from(this, data.getData());
                binding.circleImageProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            } catch(Exception e) {
                Log.d("ERROR", "Bir hata oluştu" + e.getMessage());
                Toast.makeText(this, "Bir hata oluştu " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        /**
         * FOTOĞRAF SEÇİMİ
         */
        if (requestCode == PHOTO_REQUEST_CODE_PROFILE && resultCode == RESULT_OK) {
            mImageFile = null;
            mPhotoFile = new File(mAbsolutePhotoPath);
            Picasso.with(EditProfileActivity.this).load(mPhotoPath).into(binding.circleImageProfile);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, EditProfileActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, EditProfileActivity.this);
    }

}