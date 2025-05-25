package com.example.doan; // Đảm bảo package name đúng

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.doan.fragments.LibraryFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView; // QUAN TRỌNG: Import đúng
import com.example.doan.fragments.HomeFragment;
// TODO: Import các Fragment khác khi bạn tạo chúng (SearchFragment, LibraryFragment, ...)

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

        // Đặt HomeFragment làm màn hình mặc định
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Xử lý sự kiện khi chọn item trên BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) {
                    selectedFragment = new HomeFragment();
                     } else if (itemId == R.id.navigation_library) {
                     selectedFragment = new LibraryFragment(); // Ví dụ
                     } else if (itemId == R.id.navigation_search) {
                     selectedFragment = new SearchFragment(); // Ví dụ
                     } else if (itemId == R.id.navigation_category) {
                     selectedFragment = new CategoryFragment(); // Ví dụ
                     } else if (itemId == R.id.navigation_profile) {
                     selectedFragment = new ProfileFragment(); // Ví dụ
                }
                // Thêm các else if cho các mục khác khi bạn tạo Fragment cho chúng

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });
    }

    // Hàm để tải Fragment vào fragment_container
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
// test thay đổi để push lên GitHub
