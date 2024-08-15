package com.example.hattori;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.hattori.bottom_nav_bar.AddFragment;
import com.example.hattori.bottom_nav_bar.ManageFragment;
import com.example.hattori.bottom_nav_bar.TestFragment;
import com.example.hattori.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始 Fragment
        replaceFragment(new TestFragment());

        // Bottom Navigation Bar 點擊事件
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int nowItem = item.getItemId();
            if (nowItem == R.id.bottom_nav_bar_test) {
                replaceFragment(new TestFragment());
            }
            else if (nowItem == R.id.bottom_nav_bar_add) {
                replaceFragment(new AddFragment());
            }
            else if (nowItem == R.id.bottom_nav_bar_manage) {
                replaceFragment(new ManageFragment());
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // main_activity framelayout 的 id
        fragmentTransaction.replace(R.id.mainFragment, fragment);
        fragmentTransaction.commit();
    }
}