package com.edushareteam.petshare.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.edushareteam.petshare.R;
import com.edushareteam.petshare.activities.AddProductActivity;
import com.edushareteam.petshare.activities.AddPublicationsActivity;
import com.edushareteam.petshare.activities.FavoriteActivity;
import com.edushareteam.petshare.adapters.PostsAdapter;
import com.edushareteam.petshare.adapters.PublicationsAdapter;

import com.edushareteam.petshare.databinding.FragmentPublicationsBinding;
import com.edushareteam.petshare.models.Post;
import com.edushareteam.petshare.models.Request;
import com.edushareteam.petshare.providers.AuthProvider;
import com.edushareteam.petshare.providers.PostProvider;
import com.edushareteam.petshare.providers.RequestProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;



public class PublicationsFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {

    private FragmentPublicationsBinding binding;
    AuthProvider mAuthProvider;
    RequestProvider mPostProvider;
    PublicationsAdapter mPostsAdapter;
    PublicationsAdapter mPostsAdapterSearch;

    public PublicationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPublicationsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setHasOptionsMenu(true);
        binding.recyclerViewProducts.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        mAuthProvider = new AuthProvider();
        mPostProvider = new RequestProvider();

        binding.searchBar.setOnSearchActionListener(this);
        binding.searchBar.inflateMenu(R.menu.main_menu);
        binding.searchBar.setMenuIcon(R.drawable.img);
        binding.searchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.itemFav) {
                    Intent intent = new Intent(getContext(), FavoriteActivity.class);
                    startActivity(intent);

                }
                return true;
            }
        });

        binding.fab.setOnClickListener(view1 -> goToPost());
        return view;
    }

    private void searchByTitle(String title) {
        Query query = mPostProvider.getPostByTitle(title);
        FirestoreRecyclerOptions<Request> options =
                new FirestoreRecyclerOptions.Builder<Request>()
                        .setQuery(query, Request.class)
                        .build();
        mPostsAdapterSearch = new PublicationsAdapter(options, getContext());
        mPostsAdapterSearch.notifyDataSetChanged();
        binding.recyclerViewProducts.setAdapter(mPostsAdapterSearch);
        mPostsAdapterSearch.startListening();
    }

    private void getAllPost() {
        Query query = mPostProvider.getAll();
        FirestoreRecyclerOptions<Request> options =
                new FirestoreRecyclerOptions.Builder<Request>()
                        .setQuery(query, Request.class)
                        .build();
        mPostsAdapter = new PublicationsAdapter(options, getContext());
        mPostsAdapter.notifyDataSetChanged();
        binding.recyclerViewProducts.setAdapter(mPostsAdapter);
        mPostsAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        getAllPost();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostsAdapter.stopListening();
        if (mPostsAdapterSearch != null) {
            mPostsAdapterSearch.stopListening();
        }
    }

    private void goToPost() {
        Intent intent = new Intent(getContext(), AddPublicationsActivity.class);
        startActivity(intent);
    }

    private void logout() {
        mAuthProvider.logout();
       /* Intent intent = new Intent(getContext(), IntroActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPostsAdapter.getListener() != null) {
            mPostsAdapter.getListener().remove();
        }
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled) {
            getAllPost();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        searchByTitle(text.toString());
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}