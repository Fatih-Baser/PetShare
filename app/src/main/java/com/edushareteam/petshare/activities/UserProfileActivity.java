package com.edushareteam.petshare.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.edushareteam.petshare.R;
import com.edushareteam.petshare.adapters.MyPostsAdapter;
import com.edushareteam.petshare.databinding.ActivityUserProfileBinding;
import com.edushareteam.petshare.models.Post;
import com.edushareteam.petshare.providers.AuthProvider;
import com.edushareteam.petshare.providers.ImageProvider;
import com.edushareteam.petshare.providers.PostProvider;
import com.edushareteam.petshare.providers.UsersProvider;
import com.edushareteam.petshare.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

public class UserProfileActivity extends AppCompatActivity {
    private ActivityUserProfileBinding binding;
    //Providers
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;
    String mExtraIdUser;
    MyPostsAdapter mAdapter;

    File imageFile1;

    ImageProvider mImageProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mImageProvider = new ImageProvider();
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();


        mExtraIdUser = getIntent().getStringExtra("idUser");
        if (mAuthProvider.getUid().equals(mExtraIdUser)) {
            binding.fabChat.setVisibility(View.INVISIBLE);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserProfileActivity.this);
        binding.recyclerViewMyPost.setLayoutManager(linearLayoutManager);


        imageFile1= new File("https://github.com/Fatih-Baser/KotlinMovies/blob/master/images/a.jpeg");

        binding.fabChat.setOnClickListener(view1 -> goToChatActivity());
        getUser();
        checkIfExistPost();
    }

    private void goToChatActivity() {
        Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
        intent.putExtra("idUser1", mAuthProvider.getUid());
        intent.putExtra("idUser2", mExtraIdUser);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByUser(mExtraIdUser);
        FirestoreRecyclerOptions<Post> options =
                new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        mAdapter = new MyPostsAdapter(options, UserProfileActivity.this);
        binding.recyclerViewMyPost.setAdapter(mAdapter);
        mAdapter.startListening();
        ViewedMessageHelper.updateOnline(true, UserProfileActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, UserProfileActivity.this);
    }

    private void checkIfExistPost() {
        mPostProvider.getPostByUser(mExtraIdUser).addSnapshotListener((queryDocumentSnapshots, e) -> {
            int numberPost = 0;
            if (queryDocumentSnapshots != null) {
                numberPost = queryDocumentSnapshots.size();
            }
            if (numberPost > 0) {
                binding.textViewPostExist.setText("Ürünler");
                binding.textViewPostExist.setTextColor(Color.BLACK);
            } else {
                binding.textViewPostExist.setText("Ürün yok");
                binding.textViewPostExist.setTextColor(Color.BLACK);
            }
        });
    }

    private void getUser() {
        mUsersProvider.getUser(mExtraIdUser).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("email")) {
                    String email = documentSnapshot.getString("email");
                    binding.textViewEmail.setText(email);
                }

                if (documentSnapshot.contains("username")) {
                    String username = documentSnapshot.getString("username");
                    binding.textViewUsername.setText(username);
                }

                if (documentSnapshot.contains("city")) {
                    String university = documentSnapshot.getString("city");
                    binding.textViewCity.setText(university);
                }

                if (documentSnapshot.contains("bio")) {
                    String bio = documentSnapshot.getString("bio");
                    binding.textViewBio.setText(bio);
                }
                if (documentSnapshot.contains("image")) {
                    String imageProfile = documentSnapshot.getString("image");

                    if (imageProfile != null) {
                        if (!imageProfile.isEmpty()) {
                            Picasso.with(UserProfileActivity.this).load(imageProfile).into(binding.circleImageProfile, new Callback() {
                                @Override
                                public void onSuccess() {
                                    binding.postLoading.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    binding.circleImageProfile.setImageResource(R.drawable.ic_baseline_error_24);
                                    binding.postLoading.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    }
                    else {
                        mImageProvider.save(UserProfileActivity.this, imageFile1);
                    }
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}