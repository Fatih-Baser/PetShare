package com.edushareteam.petshare.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.edushareteam.petshare.R;
import com.edushareteam.petshare.adapters.PostsAdapter;
import com.edushareteam.petshare.databinding.ActivityFilterBinding;
import com.edushareteam.petshare.models.Post;
import com.edushareteam.petshare.providers.AuthProvider;
import com.edushareteam.petshare.providers.PostProvider;
import com.edushareteam.petshare.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class FilterActivity extends AppCompatActivity {
    private ActivityFilterBinding binding;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;
    PostsAdapter mPostsAdapter;
    String mExtraCategory;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFilterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Filtre");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.recyclerViewFilter.setLayoutManager(new GridLayoutManager(FilterActivity.this, 2));

        mExtraCategory = getIntent().getStringExtra("category");

        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByCategoryAndTimestamp(mExtraCategory);
        FirestoreRecyclerOptions<Post> options =
                new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        mPostsAdapter = new PostsAdapter(options, FilterActivity.this, binding.textViewNumberFilter);
        binding.recyclerViewFilter.setAdapter(mPostsAdapter);
        mPostsAdapter.startListening();
        ViewedMessageHelper.updateOnline(true, FilterActivity.this);

    }

    @Override
    public void onStop() {
        super.onStop();
        mPostsAdapter.stopListening();
        ViewedMessageHelper.updateOnline(true, FilterActivity.this);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

}