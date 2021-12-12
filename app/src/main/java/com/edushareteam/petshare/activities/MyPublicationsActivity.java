package com.edushareteam.petshare.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.edushareteam.petshare.R;
import com.edushareteam.petshare.adapters.MyPostsAdapter;
import com.edushareteam.petshare.adapters.MyPublicationAdapter;
import com.edushareteam.petshare.adapters.PublicationsAdapter;
import com.edushareteam.petshare.databinding.ActivityMyPublicationsBinding;
import com.edushareteam.petshare.databinding.ActivityUserProfileBinding;
import com.edushareteam.petshare.models.Post;
import com.edushareteam.petshare.models.Request;
import com.edushareteam.petshare.providers.AuthProvider;
import com.edushareteam.petshare.providers.ImageProvider;
import com.edushareteam.petshare.providers.PostProvider;
import com.edushareteam.petshare.providers.RequestProvider;
import com.edushareteam.petshare.providers.UsersProvider;
import com.edushareteam.petshare.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class MyPublicationsActivity extends AppCompatActivity {
    private ActivityMyPublicationsBinding binding;

    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    RequestProvider mPostProvider;
    String mExtraIdUser;
    PublicationsAdapter mAdapter;

    ImageProvider mImageProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_publications);
        binding = ActivityMyPublicationsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.recyclerViewProducts.setLayoutManager(new GridLayoutManager(MyPublicationsActivity.this, 1));

       /* binding.mypublication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyPublicationsActivity.class);
                startActivity(intent);
            }
        });*/
        mAuthProvider = new AuthProvider();
        mPostProvider = new RequestProvider();

    }


    @Override
    public void onStart() {
        super.onStart();

        Query query = mPostProvider.getPostByUser(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Request> options =
                new FirestoreRecyclerOptions.Builder<Request>()
                        .setQuery(query, Request.class)
                        .build();
        mAdapter = new PublicationsAdapter(options, MyPublicationsActivity.this);
        mAdapter.notifyDataSetChanged();
        binding.recyclerViewProducts.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, MyPublicationsActivity.this);
    }
}