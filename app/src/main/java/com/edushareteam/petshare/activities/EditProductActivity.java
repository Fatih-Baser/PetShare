package com.edushareteam.petshare.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.edushareteam.petshare.R;
import com.edushareteam.petshare.databinding.ActivityEditProductBinding;
import com.edushareteam.petshare.fragments.DataPickerFragment;
import com.edushareteam.petshare.models.Post;
import com.edushareteam.petshare.providers.AuthProvider;
import com.edushareteam.petshare.providers.ImageProvider;
import com.edushareteam.petshare.providers.PostProvider;
import com.edushareteam.petshare.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class EditProductActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private ActivityEditProductBinding binding;
    String mExtraPostId;
    String mTitle = "";
    String mDescription = "";
    float mQuality = 0;
    String mSpinnerCategories = "";
    String currentDateString;

    File mImageFile;
    File mImageFile2;

    String mImage1;
    String mImage2;
    //Providers
    ImageProvider mImageProvider;
    PostProvider mPostProvider;
    AuthProvider mAuthProvider;

    //AlertDialog
    AlertDialog mDialog;
    AlertDialog.Builder mBuilderSelector;
    CharSequence[] options;

    private final int GALLERY_REQUEST_CODE = 1;
    private final int GALLERY_REQUEST_CODE_2 = 2;
    private final int PHOTO_REQUEST_CODE = 3;
    private final int PHOTO_REQUEST_CODE_2 = 4;

    // FOTO 1
    String mAbsolutePhotoPath;
    String mPhotoPath;
    File mPhotoFile;

    // FOTO 2
    String mAbsolutePhotoPath2;
    String mPhotoPath2;
    File mPhotoFile2;

    //Spınner
    ValueEventListener valueEventListener;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> spinnerDataList;

    //Spınner
    ValueEventListener valueEventListenercities;
    ArrayAdapter<String> arrayAdaptercities;
    ArrayList<String> spinnerDataListcities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mExtraPostId = getIntent().getStringExtra("id");

        mImageProvider = new ImageProvider();
        mPostProvider = new PostProvider();
        mAuthProvider = new AuthProvider();


        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DataPickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Lütfen biraz bekleyiniz")
                .setCancelable(false).build();

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Lütfen bir seçenek seçiniz");
        options = new CharSequence[]{"Galeriden Resmi seç", "Fotograf çek"};

        binding.circleImageBack.setOnClickListener(view1 -> finish());

        binding.btnPost.setOnClickListener(view12 -> clickEditPost());

        binding.imageViewPost1.setOnClickListener(view13 -> selectOptionImage(1));

        binding.imageViewPost2.setOnClickListener(view14 -> selectOptionImage(2));
        //extra
        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("image1") && documentSnapshot.contains("image2")) {
                    mImage1 = documentSnapshot.getString("image1");
                    mImage2 = documentSnapshot.getString("image2");
                }
            }
        });


        spinnerDataList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(EditProductActivity.this,
                android.R.layout.simple_spinner_dropdown_item, spinnerDataList);
        binding.spinnerProductCategory.setAdapter(arrayAdapter);
        retrieveSpinnerData();

        getPost();

    }

    private void retrieveSpinnerData() {
        DatabaseReference databaseReference = mPostProvider.getCategoryForSpinner();
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    spinnerDataList.add(Objects.requireNonNull(item.child("name").getValue()).toString());
                    mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.contains("category")) {
                            String category = documentSnapshot.getString("category");
                            spinnerDataList.set(0,category);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void retrieveSpinnerDatacities() {
        DatabaseReference databaseReference = mPostProvider.getCategoryForSpinner();
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    spinnerDataList.add(Objects.requireNonNull(item.child("name").getValue()).toString());
                    mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.contains("cities")) {
                            String category = documentSnapshot.getString("cities");
                            spinnerDataList.set(0,category);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getPost() {
        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {

                if (documentSnapshot.contains("image1")) {
                    String image1 = documentSnapshot.getString("image1");
                    if (image1 != null) {
                        if (!image1.isEmpty()) {
                            Picasso.with(getApplicationContext()).load(image1).into(binding.imageViewPost1);
                        }
                    }
                }

                if (documentSnapshot.contains("image2")) {
                    String image2 = documentSnapshot.getString("image2");
                    if (image2 != null) {
                        if (!image2.isEmpty()) {
                            Picasso.with(getApplicationContext()).load(image2).into(binding.imageViewPost2);
                        }
                    }
                }
                if (documentSnapshot.contains("title")) {
                    String title = documentSnapshot.getString("title");
                    assert title != null;
                    binding.textInputVideoGame.setText(title);
                }
                if (documentSnapshot.contains("description")) {
                    String description = documentSnapshot.getString("description");
                    binding.textInputDescription.setText(description);
                }
                if (documentSnapshot.contains("quality")) {
                    Long quality = documentSnapshot.getLong("quality");
                    binding.ratingBarProductQualityUpload.setRating(quality);
                }
                if (documentSnapshot.contains("expireTime")) {
                    String expireTime = documentSnapshot.getString("expireTime");
                    binding.data.setText(expireTime);
                }
            }
        });
    }

    private void clickEditPost() {
        mTitle = Objects.requireNonNull(binding.textInputVideoGame.getText()).toString();
        mDescription = Objects.requireNonNull(binding.textInputDescription.getText()).toString();
        mQuality = binding.ratingBarProductQualityUpload.getRating();

        currentDateString=Objects.requireNonNull(binding.data.getText()).toString();
        mSpinnerCategories = binding.spinnerProductCategory.getSelectedItem().toString();
        if (!mTitle.isEmpty() && !mDescription.isEmpty()) {
            // GALERİDEN İKİ RESİM SEÇİMI
            if (mImageFile != null && mImageFile2 != null) {
                saveImageAndEdit(mImageFile, mImageFile2);
            }
            // KAMERANIN İKİ RESIM CEKIMI
            else if (mPhotoFile != null && mPhotoFile2 != null) {
                saveImageAndEdit(mPhotoFile, mPhotoFile2);
                // DIGER DURUMLAR
            } else if (mImageFile != null && mPhotoFile2 != null) {
                saveImageAndEdit(mPhotoFile, mPhotoFile2);
            } else if (mPhotoFile != null && mImageFile2 != null) {
                saveImageAndEdit(mPhotoFile, mPhotoFile2);

            } else if (mPhotoFile != null) {
                saveImage(mPhotoFile, true);
            } else if (mPhotoFile2 != null) {
                saveImage(mPhotoFile2, false);
            } else if (mImageFile != null) {
                saveImage(mImageFile, true);
            } else if (mImageFile2 != null) {
                saveImage(mImageFile2, false);
            } else {
                //Toast.makeText(this, "Bir resim seçmelisiniz", Toast.LENGTH_SHORT).show();
                Post post = new Post();
                post.setTitle(mTitle);
                post.setImage1(mImage1);
                post.setImage2(mImage2);
                post.setDescription(mDescription);

                post.setExpireTime(currentDateString);
                post.setPet(mSpinnerCategories);
                post.setId(mExtraPostId);
                post.setQuality((double) mQuality);
                post.setTimestamp(new Date().getTime());
                updatePost(post);
            }
        } else {
            Toast.makeText(this, "Alanları doldurun lütfen", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageAndEdit(File imageFile1, final File imageFile2) {
        mDialog.show();
        mImageProvider.save(EditProductActivity.this, imageFile1).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    final String url = uri.toString();

                    mImageProvider.save(EditProductActivity.this, imageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                            if (taskImage2.isSuccessful()) {
                                mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri2) {
                                        final String url2 = uri2.toString();
                                        Post post = new Post();
                                        post.setImage1(url);
                                        post.setImage2(url2);
                                        post.setId(mExtraPostId);
                                        post.setTitle(mTitle);
                                        post.setDescription(mDescription);
                                        post.setPet(mSpinnerCategories);

                                        post.setExpireTime(currentDateString);
                                        post.setQuality((double) mQuality);
                                        post.setTimestamp(new Date().getTime());
                                        updatePost(post);
                                    }
                                });
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(EditProductActivity.this, "2 numaralı resim kaydedilemedi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                });
            } else {
                mDialog.dismiss();
                Toast.makeText(EditProductActivity.this, "Görüntü kaydedilirken bir hata oluştu", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveImage(File image, final boolean isProfileImage) {
        mDialog.show();
        mImageProvider.save(EditProductActivity.this, image).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    final String url = uri.toString();
                    Post post = new Post();
                    post.setTitle(mTitle);
                    post.setDescription(mDescription);
                    post.setPet(mSpinnerCategories);
                    post.setQuality((double) mQuality);
                    post.setId(mExtraPostId);
                    post.setExpireTime(currentDateString);
                    post.setTimestamp(new Date().getTime());
                    updatePost(post);
                    if (isProfileImage) {
                        post.setImage1(url);
                        post.setImage2(mImage2);
                    } else {
                        post.setImage2(url);
                        post.setImage1(mImage1);
                    }
                    post.setId(mExtraPostId);
                    updatePost(post);
                });
            } else {
                mDialog.dismiss();
                Toast.makeText(EditProductActivity.this, "Görüntü kaydedilirken bir hata oluştu", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updatePost(Post post) {
        if (mDialog.isShowing()) {
            mDialog.show();
        }
        mPostProvider.updatePost(post).addOnCompleteListener(task -> {
            mDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(EditProductActivity.this, "Bilgiler doğru bir şekilde güncellendi", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditProductActivity.this, "Bilgiler güncellenemedi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectOptionImage(final int numberImage) {

        mBuilderSelector.setItems(options, (dialogInterface, i) -> {
            if (i == 0) {
                if (numberImage == 1) {
                    openGallery(GALLERY_REQUEST_CODE);
                } else if (numberImage == 2) {
                    openGallery(GALLERY_REQUEST_CODE_2);
                }
            } else if (i == 1) {
                if (numberImage == 1) {
                    takePhoto(PHOTO_REQUEST_CODE);
                } else if (numberImage == 2) {
                    takePhoto(PHOTO_REQUEST_CODE_2);
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
                Toast.makeText(this, "Dosyada bir hata oluştu " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(EditProductActivity.this, "com.fatihbaser.edusharedemo", photoFile);
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
                storageDir);
        if (requestCode == PHOTO_REQUEST_CODE) {
            mPhotoPath = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath = photoFile.getAbsolutePath();
        } else if (requestCode == PHOTO_REQUEST_CODE_2) {
            mPhotoPath2 = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath2 = photoFile.getAbsolutePath();
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
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                mPhotoFile = null;
                mImageFile = FileUtil.from(this, data.getData());
                binding.imageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Bir hata oluştu " + e.getMessage());
                Toast.makeText(this, "Bir hata oluştu" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == GALLERY_REQUEST_CODE_2 && resultCode == RESULT_OK) {
            try {
                mPhotoFile2 = null;
                mImageFile2 = FileUtil.from(this, data.getData());
                binding.imageViewPost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Bir hata oluştu " + e.getMessage());
                Toast.makeText(this, "Bir hata oluştu " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        /**
         * FOTOĞRAF SEÇİMİ
         */
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            mImageFile = null;
            mPhotoFile = new File(mAbsolutePhotoPath);
            Picasso.with(EditProductActivity.this).load(mPhotoPath).into(binding.imageViewPost1);
        }

        /**
         * FOTOĞRAF SEÇİMİ
         */
        if (requestCode == PHOTO_REQUEST_CODE_2 && resultCode == RESULT_OK) {
            mImageFile2 = null;
            mPhotoFile2 = new File(mAbsolutePhotoPath2);
            Picasso.with(EditProductActivity.this).load(mPhotoPath2).into(binding.imageViewPost2);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        TextView textView = (TextView) findViewById(R.id.data);
        textView.setText(currentDateString);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}