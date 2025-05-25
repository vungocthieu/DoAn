package com.example.doan;

import android.os.Bundle;
import android.view.MenuItem;
// Bỏ import Menu, MenuInflater nếu không dùng onCreateOptionsMenu
// import android.view.Menu;
// import android.view.MenuInflater;
// import androidx.appcompat.widget.SearchView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.example.doan.fragments.HomeFragment;
import com.example.doan.fragments.LibraryFragment;
import com.example.doan.fragments.SearchFragment;
import com.example.doan.fragments.CategoryFragment;
import com.example.doan.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.navigation_library) {
                    selectedFragment = new LibraryFragment();
                } else if (itemId == R.id.navigation_search) {
                    selectedFragment = new SearchFragment();
                } else if (itemId == R.id.navigation_category) {
                    selectedFragment = new CategoryFragment();
                } else if (itemId == R.id.navigation_profile) {
                    selectedFragment = new ProfileFragment();
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    // KHÔNG CÒN PHƯƠNG THỨC onCreateOptionsMenu VÀ performSearch Ở ĐÂY NỮA
}