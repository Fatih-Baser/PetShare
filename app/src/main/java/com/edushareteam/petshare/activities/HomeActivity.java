package com.edushareteam.petshare.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.edushareteam.petshare.R;
import com.edushareteam.petshare.fragments.ChatsFragment;
import com.edushareteam.petshare.fragments.PetsFragment;
import com.edushareteam.petshare.fragments.ProductsFragment;
import com.edushareteam.petshare.fragments.ProfilFragment;
import com.edushareteam.petshare.fragments.PublicationsFragment;
import com.edushareteam.petshare.providers.AuthProvider;
import com.edushareteam.petshare.providers.TokenProvider;
import com.edushareteam.petshare.providers.UsersProvider;
import com.edushareteam.petshare.utils.ViewedMessageHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    TokenProvider mTokenProvider;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        mTokenProvider = new TokenProvider();
        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();

        openFragment(new ProductsFragment());
        createToken();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, HomeActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, HomeActivity.this);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                if (item.getItemId() == R.id.itemHome) {
                    // FRAGMENT HOME
                    openFragment(new ProductsFragment());
                } else if (item.getItemId() == R.id.itemUsers) {
                    // FRAGMENT CHATS
                    openFragment(new PetsFragment());

                } else if (item.getItemId() == R.id.itemChats) {
                    // FRAGMENT CHATS
                    openFragment(new PublicationsFragment());

                } else if (item.getItemId() == R.id.itemFilters) {
                    // FRAGMENT FILTROS
                    openFragment(new ChatsFragment());

                } else if (item.getItemId() == R.id.itemProfile) {
                    // FRAGMENT PROFILE
                    openFragment(new ProfilFragment());
                }
                return true;
            };

    private void createToken() {
        mTokenProvider.create(mAuthProvider.getUid());
    }
}