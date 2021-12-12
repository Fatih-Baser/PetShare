package com.edushareteam.petshare.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.edushareteam.petshare.databinding.ActivityRegisterBinding;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    AuthProvider mAuthProvider;
    ImageProvider mImageProvider;
    UsersProvider mUsersProvider;


    AlertDialog.Builder mBuilderSelector;
    AlertDialog mDialog;
    CharSequence options[];
    private final int GALLERY_REQUEST_CODE_PROFILE = 1;
    private final int PHOTO_REQUEST_CODE_PROFILE = 3;


    // FOTO 1
    String mAbsolutePhotoPath;
    String mPhotoPath;
    File mPhotoFile;
    File mImageFile;

    String mSpinnerUniversity = "";
    String mUsername = "";
    String mUniversity = "";
    String mDepartment = "";
    String mBio = "";
    String mImageProfile = "";
    DatabaseReference databaseReference;

    //Spınner
    ValueEventListener valueEventListener;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> spinnerDataList;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //Providers
        databaseReference = FirebaseDatabase.getInstance().getReference("cities");
        mAuthProvider = new AuthProvider();
        mImageProvider = new ImageProvider();
        mUsersProvider = new UsersProvider();

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Please select an option");
        options = new CharSequence[]{"Choose picture from gallery", "Take photo"};

        spinnerDataList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(RegisterActivity.this,
                android.R.layout.simple_spinner_dropdown_item, spinnerDataList);
        binding.spinnerProductCategory.setAdapter(arrayAdapter);
        retrieveData();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Registering ...")
                .setCancelable(false).build();

        binding.imageViewProfile.setOnClickListener(view1 -> selectOptionImage(1));

        binding.btnRegister.setOnClickListener(view12 -> register());

        binding.back.setOnClickListener(view13 -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        });

    }
    private void retrieveData() {

        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    spinnerDataList.add(item.child("name").getValue().toString());
                }
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void register() {
        String username = Objects.requireNonNull(binding.textInputUsername.getText()).toString();
        String email = Objects.requireNonNull(binding.textInputEmail.getText()).toString();
        String city = Objects.requireNonNull(binding.spinnerProductCategory.getSelectedItem()).toString();


        String bio = Objects.requireNonNull(binding.bio.getText()).toString();
        String password = Objects.requireNonNull(binding.textInputPassword.getText()).toString();
        String confirmPassword = Objects.requireNonNull(binding.textInputConfirmPassword.getText()).toString();

        if (mImageFile != null) {
            if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !city.isEmpty() && !bio.isEmpty() && !confirmPassword.isEmpty() && mImageFile != null) {
                if (isEmailValid(email)) {
                    if (password.equals(confirmPassword)) {
                        if (password.length() >= 6) {
                            createUser(username, email, password, city, bio, mImageFile);
                        } else {
                            Toast.makeText(this, "Your password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "You have filled in all the fields but the email is not valid", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Add all fields to continue", Toast.LENGTH_SHORT).show();
            }
        } else if (mPhotoFile != null) {
            if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !city.isEmpty() && !bio.isEmpty() && !confirmPassword.isEmpty() && mPhotoFile != null) {
                if (isEmailValid(email)) {
                    if (password.equals(confirmPassword)) {
                        if (password.length() >= 6) {
                            createUser(username, email, password, city, bio, mPhotoFile);
                        } else {
                            Toast.makeText(this, "Your password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "You have filled in all the fields but the email is not valid", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Add all fields to continue", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void createUser(final String username, final String email, final String password, String city, final String bio, File imageFile1) {
        mDialog.show();
        mAuthProvider.register(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String id = mAuthProvider.getUid();

                User user = new User();
                user.setId(id);
                user.setEmail(email);

                mUsersProvider.create(user).addOnCompleteListener(task1 -> {
                    mDialog.dismiss();
                    if (task1.isSuccessful()) {
                        mImageProvider.save(RegisterActivity.this, imageFile1).addOnCompleteListener(task11 -> {
                            if (task11.isSuccessful()) {
                                mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                                    User user1 = new User();
                                    final String urlProfile = uri.toString();
                                    String id1 = mAuthProvider.getUid();
                                    user1.setId(id1);
                                    user1.setImage(urlProfile);
                                    user1.setUsername(username);
                                    user1.setCity(city);
                                    user1.setBio(bio);
                                    user1.setTimestamp(new Date().getTime());
                                    mUsersProvider.updateProfile(user1).addOnCompleteListener(task111 -> {
                                        mDialog.dismiss();
                                        if (task111.isSuccessful()) {
                                            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "The user could not be stored in the database", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                });
                            }
                        });
                    } else {
                        Toast.makeText(RegisterActivity.this, "Mail User could not be stored in database", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                mDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "User failed to register", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    private void selectOptionImage(final int numberImage) {

        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    if (numberImage == 1) {
                        openGallery(GALLERY_REQUEST_CODE_PROFILE);
                    }
                } else if (i == 1) {
                    if (numberImage == 1) {
                        takePhoto(PHOTO_REQUEST_CODE_PROFILE);
                    }
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
            } catch (Exception e) {
                Toast.makeText(this, "An error occurred in the file " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(RegisterActivity.this, "com.fatihbaser.edusharedemo", photoFile);
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
                binding.imageViewProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Something went wrong" + e.getMessage());
                Toast.makeText(this, "Something went wrong " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        /**
         * FOTOĞRAF SEÇİMİ
         */
        if (requestCode == PHOTO_REQUEST_CODE_PROFILE && resultCode == RESULT_OK) {
            mImageFile = null;
            mPhotoFile = new File(mAbsolutePhotoPath);
            Picasso.with(RegisterActivity.this).load(mPhotoPath).into(binding.imageViewProfile);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, RegisterActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, RegisterActivity.this);
    }

}