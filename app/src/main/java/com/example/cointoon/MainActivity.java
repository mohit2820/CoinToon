package com.example.cointoon;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cointoon.databinding.ActivityMainBinding;

import me.ibrahimsn.lib.OnItemSelectedListener;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // default fragment
        loadFragment(new DashboardFragment(), "DASHBOARD");

        // bottom nav
        binding.bottomNav.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {

                switch (i) {
                    case 0:
                        loadFragment(new DashboardFragment(), "DASHBOARD");
                        break;
                    case 1:
                        loadFragment(new HistoryFragment(), "HISTORY");
                        break;

                    case 2:
                        loadFragment(new ChartFragment(), "CHART");
                        break;
                }


                return true;
            }
        });


    }

    public void selectTab(int index) {
        binding.bottomNav.setItemActiveIndex(index);

        switch(index) {
            case 0: loadFragment(new DashboardFragment(), "DASHBOARD"); break;
            case 1: loadFragment(new HistoryFragment(), "HISTORY"); break;
            case 2: loadFragment(new ChartFragment(), "CHART"); break;
        }
    }


    private void loadFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

        Fragment existingFragment = getSupportFragmentManager().findFragmentByTag(tag);

        // Hide all existing fragments
        for (Fragment frag : getSupportFragmentManager().getFragments()) {
            transaction.hide(frag);
        }

        if (existingFragment != null) {
            transaction.show(existingFragment);
        } else {
            transaction.add(R.id.fragment_container, fragment, tag);
        }

        transaction.commit();
    }

}